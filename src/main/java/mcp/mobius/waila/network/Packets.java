package mcp.mobius.waila.network;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

    public static final ResourceLocation DATA_RAW = Waila.id("data");
    public static final ResourceLocation DATA_TYPED = Waila.id("data_typed");
    public static final ResourceLocation CONFIG = Waila.id("config");
    public static final ResourceLocation BLACKLIST = Waila.id("blacklist");
    public static final ResourceLocation GENERATE_CLIENT_DUMP = Waila.id("generate_client_dump");

    private static final Gson GSON = new Gson();

    public static void initServer() {
        PacketSenderReadyCallback.registerServer((handler, sender, server) -> {
            FriendlyByteBuf versionBuf = new FriendlyByteBuf(Unpooled.buffer());
            versionBuf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, versionBuf);


            FriendlyByteBuf blacklistBuf = new FriendlyByteBuf(Unpooled.buffer());
            BlacklistConfig blacklistConfig = Waila.BLACKLIST_CONFIG.get();
            writeIds(blacklistBuf, blacklistConfig.blockIds);
            writeIds(blacklistBuf, blacklistConfig.blockEntityTypeIds);
            writeIds(blacklistBuf, blacklistConfig.entityTypeIds);
            sender.send(BLACKLIST, blacklistBuf);

            FriendlyByteBuf configBuf = new FriendlyByteBuf(Unpooled.buffer());
            Map<String, List<ConfigEntry<Object>>> groups = PluginConfig.getSyncableConfigs().stream()
                .collect(Collectors.groupingBy(c -> c.getId().getNamespace()));
            configBuf.writeVarInt(groups.size());
            groups.forEach((namespace, entries) -> {
                configBuf.writeUtf(namespace);
                configBuf.writeVarInt(entries.size());
                entries.forEach(e -> {
                    configBuf.writeUtf(e.getId().getPath());
                    Object v = e.getLocalValue();
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
            int clientVersion = buf.readVarInt();

            if (clientVersion != NETWORK_VERSION) {
                handler.disconnect(Component.literal(
                    WailaConstants.MOD_NAME + " network version mismatch! " +
                        "Server version is " + NETWORK_VERSION + " while client version is " + clientVersion));
            }
        });

        C2SPacketReceiver.register(ENTITY, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            Vec3 hitPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

            server.execute(() -> {
                Registrar registrar = Registrar.INSTANCE;
                Level world = player.level();
                Entity entity = world.getEntity(entityId);

                if (entity == null) {
                    return;
                }

                CompoundTag raw = DataWriter.INSTANCE.reset();
                IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, hitPos), entity);

                for (IDataProvider<Entity> provider : registrar.entityData.get(entity)) {
                    tryAppendData(provider, accessor);
                }

                raw.putInt("WailaEntityID", entity.getId());
                raw.putLong("WailaTime", System.currentTimeMillis());

                FriendlyByteBuf rawBuf = new FriendlyByteBuf(Unpooled.buffer());
                rawBuf.writeNbt(raw);
                responseSender.send(DATA_RAW, rawBuf);

                DataWriter.INSTANCE.sendTypedPackets(responseSender, player);
            });
        });

        C2SPacketReceiver.register(BLOCK, (server, player, handler, buf, responseSender) -> {
            BlockHitResult hitResult = buf.readBlockHitResult();

            server.execute(() -> {
                Registrar registrar = Registrar.INSTANCE;
                Level world = player.level();
                BlockPos pos = hitResult.getBlockPos();

                //noinspection deprecation
                if (!world.hasChunkAt(pos)) {
                    return;
                }

                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity == null) {
                    return;
                }

                BlockState state = world.getBlockState(pos);
                CompoundTag raw = DataWriter.INSTANCE.reset();
                IServerAccessor<BlockEntity> accessor = ServerAccessor.INSTANCE.set(world, player, hitResult, blockEntity);

                for (IDataProvider<BlockEntity> provider : registrar.blockData.get(blockEntity)) {
                    tryAppendData(provider, accessor);
                }

                for (IDataProvider<BlockEntity> provider : registrar.blockData.get(state.getBlock())) {
                    tryAppendData(provider, accessor);
                }

                raw.putInt("x", pos.getX());
                raw.putInt("y", pos.getY());
                raw.putInt("z", pos.getZ());
                //noinspection ConstantConditions
                raw.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());
                raw.putLong("WailaTime", System.currentTimeMillis());

                FriendlyByteBuf rawBuf = new FriendlyByteBuf(Unpooled.buffer());
                rawBuf.writeNbt(raw);
                responseSender.send(DATA_RAW, rawBuf);

                DataWriter.INSTANCE.sendTypedPackets(responseSender, player);
            });
        });
    }

    public static void initClient() {
        PacketSenderReadyCallback.registerClient((handler, sender, client) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, buf);
        });

        S2CPacketReceiver.register(VERSION, (client, handler, buf, responseSender) -> {
            int serverVersion = buf.readVarInt();
            if (serverVersion != NETWORK_VERSION) {
                handler.getConnection().disconnect(Component.literal(
                    WailaConstants.MOD_NAME + " network version mismatch! " +
                        "Server version is " + serverVersion + " while client version is " + NETWORK_VERSION));
            }
        });

        S2CPacketReceiver.register(DATA_RAW, (client, handler, buf, responseSender) -> {
            CompoundTag data = buf.readNbt();

            client.execute(() -> DataReader.INSTANCE.reset(data));
        });

        S2CPacketReceiver.register(DATA_TYPED, (client, handler, buf, responseSender) -> {
            IData data = DataReader.readTypedPacket(buf);

            client.execute(() -> DataReader.INSTANCE.add(data));
        });

        S2CPacketReceiver.register(CONFIG, (client, handler, buf, responseSender) -> {
            Map<ResourceLocation, Object> map = new HashMap<>();
            int groupSize = buf.readVarInt();
            for (int i = 0; i < groupSize; i++) {
                String namespace = buf.readUtf();
                int groupLen = buf.readVarInt();
                for (int j = 0; j < groupLen; j++) {
                    ResourceLocation id = new ResourceLocation(namespace, buf.readUtf());
                    byte type = buf.readByte();
                    switch (type) {
                        case CONFIG_BOOL -> map.put(id, buf.readBoolean());
                        case CONFIG_INT -> map.put(id, buf.readVarInt());
                        case CONFIG_DOUBLE -> map.put(id, buf.readDouble());
                        case CONFIG_STRING -> map.put(id, buf.readUtf());
                    }
                }
            }

            client.execute(() -> {
                for (ConfigEntry<Object> config : PluginConfig.getSyncableConfigs()) {
                    ResourceLocation id = config.getId();
                    Object clientOnlyValue = config.getClientOnlyValue();
                    Object syncedValue = clientOnlyValue instanceof Enum<?> e
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
            Set<ResourceLocation> blockIds = readIds(buf);
            Set<ResourceLocation> blockEntityIds = readIds(buf);
            Set<ResourceLocation> entityIds = readIds(buf);

            client.execute(() -> {
                BlacklistConfig blacklist = Waila.BLACKLIST_CONFIG.get();
                setBlackList(blockIds, blacklist.blocks, BuiltInRegistries.BLOCK);
                setBlackList(blockEntityIds, blacklist.blockEntityTypes, BuiltInRegistries.BLOCK_ENTITY_TYPE);
                setBlackList(entityIds, blacklist.entityTypes, BuiltInRegistries.ENTITY_TYPE);
            });
        });

        S2CPacketReceiver.register(GENERATE_CLIENT_DUMP, (client, handler, buf, responseSender) -> client.execute(() -> {
            Path path = DumpGenerator.generate(DumpGenerator.CLIENT);
            if (path != null && client.player != null) {
                Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())));
                client.player.displayClientMessage(Component.translatable(Tl.Command.CLIENT_DUMP_SUCCESS, pathComponent), false);
            }
        }));
    }

    private static <T> void setBlackList(Set<ResourceLocation> ids, Set<T> set, Registry<T> registry) {
        for (ResourceLocation id : ids) {
            set.add(registry.get(id));
        }
    }

    private static void writeIds(FriendlyByteBuf buf, Set<ResourceLocation> set) {
        Map<String, List<ResourceLocation>> groups = set.stream().collect(Collectors.groupingBy(ResourceLocation::getNamespace));
        buf.writeVarInt(groups.size());
        groups.forEach((namespace, ids) -> {
            buf.writeUtf(namespace);
            buf.writeVarInt(ids.size());
            ids.forEach(id -> buf.writeUtf(id.getPath()));
        });
    }

    private static Set<ResourceLocation> readIds(FriendlyByteBuf buf) {
        Set<ResourceLocation> set = new HashSet<>();
        int groupSize = buf.readVarInt();
        for (int i = 0; i < groupSize; i++) {
            String namespace = buf.readUtf();
            int groupLen = buf.readVarInt();
            for (int j = 0; j < groupLen; j++) {
                set.add(new ResourceLocation(namespace, buf.readUtf()));
            }
        }
        return set;
    }

    private static <T> void tryAppendData(IDataProvider<T> provider, IServerAccessor<T> accessor) {
        try {
            provider.appendData(DataWriter.INSTANCE, accessor, PluginConfig.SERVER);
        } catch (Throwable t) {
            ServerPlayer player = accessor.getPlayer();

            if (ExceptionUtil.dump(t, provider.getClass().toString() + "\nplayer " + player.getScoreboardName(), null)) {
                player.sendSystemMessage(Component.literal("Error on retrieving server data from provider " + provider.getClass().getName()));
            }
        }
    }

}
