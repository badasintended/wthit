package mcp.mobius.waila.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.C2SPacketReceiver;
import lol.bai.badpackets.api.S2CPacketReceiver;
import lol.bai.badpackets.api.event.PacketSenderReadyCallback;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_INT;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;
import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class Packets {

    private static final Log LOG = Log.create();

    public static final ResourceLocation VERSION = Waila.id("version");

    public static final ResourceLocation ENTITY = Waila.id("entity");
    public static final ResourceLocation BLOCK = Waila.id("block");
    public static final ResourceLocation CTX_RAW = Waila.id("ctx");
    public static final ResourceLocation CTX_TYPED = Waila.id("ctx_typed");

    public static final ResourceLocation DATA_RAW = Waila.id("data");
    public static final ResourceLocation DATA_TYPED = Waila.id("data_typed");
    public static final ResourceLocation CONFIG = Waila.id("config");
    public static final ResourceLocation BLACKLIST = Waila.id("blacklist");
    public static final ResourceLocation GENERATE_CLIENT_DUMP = Waila.id("generate_client_dump");

    private static final Gson GSON = new Gson();

    public static void initServer() {
        PacketSenderReadyCallback.registerServer((handler, sender, server) -> {
            var versionBuf = new FriendlyByteBuf(Unpooled.buffer());
            versionBuf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, versionBuf);


            var blacklistBuf = new FriendlyByteBuf(Unpooled.buffer());
            var blacklist = Waila.BLACKLIST_CONFIG.get();
            blacklistBuf.writeCollection(blacklist.blocks, FriendlyByteBuf::writeUtf);
            blacklistBuf.writeCollection(blacklist.blockEntityTypes, FriendlyByteBuf::writeUtf);
            blacklistBuf.writeCollection(blacklist.entityTypes, FriendlyByteBuf::writeUtf);
            sender.send(BLACKLIST, blacklistBuf);

            var configBuf = new FriendlyByteBuf(Unpooled.buffer());
            var groups = PluginConfig.getSyncableConfigs().stream()
                .collect(Collectors.groupingBy(c -> c.getId().getNamespace()));
            configBuf.writeVarInt(groups.size());
            groups.forEach((namespace, entries) -> {
                configBuf.writeUtf(namespace);
                configBuf.writeVarInt(entries.size());
                entries.forEach(e -> {
                    configBuf.writeUtf(e.getId().getPath());
                    var v = e.getLocalValue();
                    if (v instanceof Boolean z) {
                        configBuf.writeByte(CONFIG_BOOL);
                        configBuf.writeBoolean(z);
                    } else if (v instanceof Integer i) {
                        configBuf.writeByte(CONFIG_INT);
                        configBuf.writeVarInt(i);
                    } else if (v instanceof Double d) {
                        configBuf.writeByte(CONFIG_DOUBLE);
                        configBuf.writeDouble(d);
                    } else if (v instanceof String str) {
                        configBuf.writeByte(CONFIG_STRING);
                        configBuf.writeUtf(str);
                    } else if (v instanceof Enum<?> en) {
                        configBuf.writeByte(CONFIG_STRING);
                        configBuf.writeUtf(en.name());
                    }
                });
            });
            sender.send(CONFIG, configBuf);
        });

        C2SPacketReceiver.register(VERSION, (server, player, handler, buf, responseSender) -> {
            var clientVersion = buf.readVarInt();

            if (clientVersion != NETWORK_VERSION) {
                handler.disconnect(Component.literal(
                    WailaConstants.MOD_NAME + " network version mismatch! " +
                        "Server version is " + NETWORK_VERSION + " while client version is " + clientVersion));
            }
        });

        C2SPacketReceiver.register(CTX_RAW, (server, player, handler, buf, responseSender) -> {
            var ctx = buf.readNbt();

            server.execute(() -> DataReader.SERVER.reset(ctx));
        });

        C2SPacketReceiver.register(CTX_TYPED, (server, player, handler, buf, responseSender) -> {
            var ctx = DataReader.readTypedPacket(buf);

            server.execute(() -> DataReader.SERVER.add(ctx));
        });

        C2SPacketReceiver.register(ENTITY, (server, player, handler, buf, responseSender) -> {
            var entityId = buf.readVarInt();
            var hitPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

            server.execute(() -> {
                var registrar = Registrar.INSTANCE;
                var world = player.level();
                var entity = world.getEntity(entityId);

                if (entity == null) {
                    return;
                }

                var raw = DataWriter.SERVER.reset();
                IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, hitPos), entity);

                for (var provider : registrar.entityData.get(entity)) {
                    tryAppendData(provider.value(), accessor);
                }

                raw.putInt("WailaEntityID", entity.getId());
                raw.putLong("WailaTime", System.currentTimeMillis());

                DataWriter.SERVER.send(responseSender, player);
            });
        });

        C2SPacketReceiver.register(BLOCK, (server, player, handler, buf, responseSender) -> {
            var hitResult = buf.readBlockHitResult();

            server.execute(() -> {
                var registrar = Registrar.INSTANCE;
                var world = player.level();
                var pos = hitResult.getBlockPos();

                //noinspection deprecation
                if (!world.hasChunkAt(pos)) {
                    return;
                }

                var blockEntity = world.getBlockEntity(pos);
                if (blockEntity == null) {
                    return;
                }

                var state = world.getBlockState(pos);
                var raw = DataWriter.SERVER.reset();
                IServerAccessor<BlockEntity> accessor = ServerAccessor.INSTANCE.set(world, player, hitResult, blockEntity);

                for (var provider : registrar.blockData.get(blockEntity)) {
                    tryAppendData(provider.value(), accessor);
                }

                for (var provider : registrar.blockData.get(state.getBlock())) {
                    tryAppendData(provider.value(), accessor);
                }

                raw.putInt("x", pos.getX());
                raw.putInt("y", pos.getY());
                raw.putInt("z", pos.getZ());
                //noinspection ConstantConditions
                raw.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());
                raw.putLong("WailaTime", System.currentTimeMillis());

                DataWriter.SERVER.send(responseSender, player);
            });
        });
    }

    public static void initClient() {
        PacketSenderReadyCallback.registerClient((handler, sender, client) -> {
            var buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, buf);
        });

        S2CPacketReceiver.register(VERSION, (client, handler, buf, responseSender) -> {
            var serverVersion = buf.readVarInt();
            if (serverVersion != NETWORK_VERSION) {
                handler.getConnection().disconnect(Component.literal(
                    WailaConstants.MOD_NAME + " network version mismatch! " +
                        "Server version is " + serverVersion + " while client version is " + NETWORK_VERSION));
            }
        });

        S2CPacketReceiver.register(DATA_RAW, (client, handler, buf, responseSender) -> {
            var data = buf.readNbt();

            client.execute(() -> DataReader.CLIENT.reset(data));
        });

        S2CPacketReceiver.register(DATA_TYPED, (client, handler, buf, responseSender) -> {
            var data = DataReader.readTypedPacket(buf);

            client.execute(() -> DataReader.CLIENT.add(data));
        });

        S2CPacketReceiver.register(CONFIG, (client, handler, buf, responseSender) -> {
            Map<ResourceLocation, Object> map = new HashMap<>();
            var groupSize = buf.readVarInt();
            for (var i = 0; i < groupSize; i++) {
                var namespace = buf.readUtf();
                var groupLen = buf.readVarInt();
                for (var j = 0; j < groupLen; j++) {
                    var id = new ResourceLocation(namespace, buf.readUtf());
                    var type = buf.readByte();
                    switch (type) {
                        case CONFIG_BOOL -> map.put(id, buf.readBoolean());
                        case CONFIG_INT -> map.put(id, buf.readVarInt());
                        case CONFIG_DOUBLE -> map.put(id, buf.readDouble());
                        case CONFIG_STRING -> map.put(id, buf.readUtf());
                    }
                }
            }

            client.execute(() -> {
                for (var config : PluginConfig.getSyncableConfigs()) {
                    var id = config.getId();
                    var clientOnlyValue = config.getClientOnlyValue();
                    var syncedValue = clientOnlyValue instanceof Enum<?> e
                        ? Enum.valueOf(e.getDeclaringClass(), (String) map.getOrDefault(id, e.name()))
                        : map.get(id);
                    if (syncedValue instanceof Double d && clientOnlyValue instanceof Integer) {
                        syncedValue = d.intValue();
                    }
                    config.setServerValue(syncedValue);
                }
                LOG.info("Received config from the server: {}", GSON.toJson(map));
            });
        });

        S2CPacketReceiver.register(BLACKLIST, (client, handler, buf, responseSender) -> {
            Set<String> blockRules = buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf);
            Set<String> blockEntityRules = buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf);
            Set<String> entityRules = buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf);

            client.execute(() ->
                Waila.BLACKLIST_CONFIG.get().getView().sync(blockRules, blockEntityRules, entityRules));
        });

        S2CPacketReceiver.register(GENERATE_CLIENT_DUMP, (client, handler, buf, responseSender) -> client.execute(() -> {
            var path = DumpGenerator.generate(DumpGenerator.CLIENT);
            if (path != null && client.player != null) {
                Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())));
                client.player.displayClientMessage(Component.translatable(Tl.Command.CLIENT_DUMP_SUCCESS, pathComponent), false);
            }
        }));
    }

    private static <T> void tryAppendData(IDataProvider<T> provider, IServerAccessor<T> accessor) {
        try {
            provider.appendData(DataWriter.SERVER, accessor, PluginConfig.SERVER);
        } catch (Throwable t) {
            var player = accessor.getPlayer();

            if (ExceptionUtil.dump(t, provider.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                player.sendSystemMessage(Component.literal("Error on retrieving server data from provider " + provider.getClass().getName()));
            }
        }
    }

}
