package mcp.mobius.waila.network;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.C2SPacketReceiver;
import lol.bai.badpackets.api.S2CPacketReceiver;
import lol.bai.badpackets.api.event.PacketSenderReadyCallback;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Packets {

    static final int NETWORK_VERSION = 3;

    public static final ResourceLocation VERSION = Waila.id("version");

    public static final ResourceLocation ENTITY = Waila.id("entity");
    public static final ResourceLocation BLOCK = Waila.id("block");

    public static final ResourceLocation DATA = Waila.id("data");
    public static final ResourceLocation CONFIG = Waila.id("config");
    public static final ResourceLocation BLACKLIST = Waila.id("blacklist");
    public static final ResourceLocation GENERATE_CLIENT_DUMP = Waila.id("generate_client_dump");

    static final int CONFIG_BOOL = 0;
    static final int CONFIG_INT = 1;
    static final int CONFIG_DOUBLE = 2;
    static final int CONFIG_STRING = 3;

    private static final Gson GSON = new Gson();

    public static void initServer() {
        PacketSenderReadyCallback.registerServer((handler, sender, server) -> {
            FriendlyByteBuf versionBuf = new FriendlyByteBuf(Unpooled.buffer());
            versionBuf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, versionBuf);

            FriendlyByteBuf blacklistBuf = new FriendlyByteBuf(Unpooled.buffer());
            BlacklistConfig blacklistConfig = Waila.BLACKLIST_CONFIG.get();
            writeRawIds(blacklistBuf, blacklistConfig.blocks, Registry.BLOCK);
            writeRawIds(blacklistBuf, blacklistConfig.blockEntityTypes, Registry.BLOCK_ENTITY_TYPE);
            writeRawIds(blacklistBuf, blacklistConfig.entityTypes, Registry.ENTITY_TYPE);
            sender.send(BLACKLIST, blacklistBuf);

            FriendlyByteBuf configBuf = new FriendlyByteBuf(Unpooled.buffer());
            Set<ConfigEntry<Object>> entries = PluginConfig.INSTANCE.getSyncableConfigs();
            configBuf.writeVarInt(entries.size());
            for (ConfigEntry<Object> e : entries) {
                configBuf.writeResourceLocation(e.getId());

                Object v = e.getValue();
                if (v instanceof Boolean z) {
                    configBuf.writeVarInt(CONFIG_BOOL);
                    configBuf.writeBoolean(z);
                } else if (v instanceof Integer i) {
                    configBuf.writeVarInt(CONFIG_INT);
                    configBuf.writeVarInt(i);
                } else if (v instanceof Double d) {
                    configBuf.writeVarInt(CONFIG_DOUBLE);
                    configBuf.writeDouble(d);
                } else if (v instanceof String str) {
                    configBuf.writeVarInt(CONFIG_STRING);
                    configBuf.writeUtf(str);
                } else if (v instanceof Enum<?> en) {
                    configBuf.writeVarInt(CONFIG_STRING);
                    configBuf.writeUtf(en.name());
                }
            }
            sender.send(CONFIG, configBuf);
        });

        C2SPacketReceiver.register(VERSION, (server, player, handler, buf, responseSender) -> {
            int clientVersion = buf.readVarInt();

            server.execute(() -> {
                if (clientVersion != NETWORK_VERSION) {
                    handler.disconnect(new TextComponent(
                        WailaConstants.MOD_NAME + " network version mismatch! " +
                            "Server version is " + NETWORK_VERSION + " while client version is " + clientVersion));
                }
            });
        });

        C2SPacketReceiver.register(ENTITY, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            Vec3 hitPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

            server.execute(() -> {
                Registrar registrar = Registrar.INSTANCE;
                Level world = player.level;
                Entity entity = world.getEntity(entityId);

                if (entity == null) {
                    return;
                }

                CompoundTag data = new CompoundTag();
                IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, hitPos), entity);

                for (IServerDataProvider<Entity> provider : registrar.entityData.get(entity)) {
                    provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
                    //noinspection deprecation
                    provider.appendServerData(data, player, world, entity);
                }

                data.putInt("WailaEntityID", entity.getId());

                FriendlyByteBuf dataBuf = new FriendlyByteBuf(Unpooled.buffer());
                dataBuf.writeNbt(data);
                responseSender.send(DATA, dataBuf);
            });
        });

        C2SPacketReceiver.register(BLOCK, (server, player, handler, buf, responseSender) -> {
            BlockHitResult hitResult = buf.readBlockHitResult();

            server.execute(() -> {
                Registrar registrar = Registrar.INSTANCE;
                Level world = player.level;
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
                CompoundTag data = new CompoundTag();
                IServerAccessor<BlockEntity> accessor = ServerAccessor.INSTANCE.set(world, player, hitResult, blockEntity);

                for (IServerDataProvider<BlockEntity> provider : registrar.blockData.get(blockEntity)) {
                    provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
                    //noinspection deprecation
                    provider.appendServerData(data, player, world, blockEntity);
                }

                for (IServerDataProvider<BlockEntity> provider : registrar.blockData.get(state.getBlock())) {
                    provider.appendServerData(data, accessor, PluginConfig.INSTANCE);
                    //noinspection deprecation
                    provider.appendServerData(data, player, world, blockEntity);
                }

                data.putInt("x", pos.getX());
                data.putInt("y", pos.getY());
                data.putInt("z", pos.getZ());
                //noinspection ConstantConditions
                data.putString("id", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

                FriendlyByteBuf dataBuf = new FriendlyByteBuf(Unpooled.buffer());
                dataBuf.writeNbt(data);
                responseSender.send(DATA, dataBuf);
            });
        });
    }

    public static void initClient() {
        PacketSenderReadyCallback.registerClient((handler, sender, client) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeVarInt(NETWORK_VERSION);
            sender.send(VERSION, buf);

            if (!sender.canSend(VERSION)) {
                Waila.LOGGER.warn("WTHIT is not found on the server, all syncable config will reset to their client-only value.");
                PluginConfig.INSTANCE.getSyncableConfigs().forEach(config ->
                    config.setValue(config.getClientOnlyValue()));
            }
        });

        S2CPacketReceiver.register(VERSION, (client, handler, buf, responseSender) -> {
            int serverVersion = buf.readVarInt();

            client.execute(() -> {
                if (serverVersion != NETWORK_VERSION) {
                    handler.getConnection().disconnect(new TextComponent(
                        WailaConstants.MOD_NAME + " network version mismatch! " +
                            "Server version is " + serverVersion + " while client version is " + NETWORK_VERSION));
                }
            });
        });

        S2CPacketReceiver.register(DATA, (client, handler, buf, responseSender) -> {
            CompoundTag data = buf.readNbt();

            client.execute(() -> DataAccessor.INSTANCE.setServerData(data));
        });

        S2CPacketReceiver.register(CONFIG, (client, handler, buf, responseSender) -> {
            int size = buf.readVarInt();
            Map<ResourceLocation, Object> map = new HashMap<>();
            for (int j = 0; j < size; j++) {
                ResourceLocation id = buf.readResourceLocation();
                int type = buf.readVarInt();
                switch (type) {
                    case CONFIG_BOOL -> map.put(id, buf.readBoolean());
                    case CONFIG_INT -> map.put(id, buf.readVarInt());
                    case CONFIG_DOUBLE -> map.put(id, buf.readDouble());
                    case CONFIG_STRING -> map.put(id, buf.readUtf());
                }
            }

            client.execute(() -> {
                for (ConfigEntry<Object> config : PluginConfig.INSTANCE.getSyncableConfigs()) {
                    ResourceLocation id = config.getId();
                    Object defaultValue = config.getDefaultValue();
                    Object syncedValue = defaultValue instanceof Enum<?> e
                        ? Enum.valueOf(e.getDeclaringClass(), (String) map.getOrDefault(id, e.name()))
                        : map.getOrDefault(id, defaultValue);
                    config.setValue(syncedValue);
                }
                Waila.LOGGER.info("Received config from the server: {}", GSON.toJson(map));
            });
        });

        S2CPacketReceiver.register(BLACKLIST, (client, handler, buf, responseSender) -> {
            int[] blockIds = buf.readVarIntArray();
            int[] blockEntityIds = buf.readVarIntArray();
            int[] entityIds = buf.readVarIntArray();

            client.execute(() -> {
                BlacklistConfig blacklist = Waila.BLACKLIST_CONFIG.get();
                setBlackList(blockIds, blacklist.blocks, Registry.BLOCK);
                setBlackList(blockEntityIds, blacklist.blockEntityTypes, Registry.BLOCK_ENTITY_TYPE);
                setBlackList(entityIds, blacklist.entityTypes, Registry.ENTITY_TYPE);
            });
        });

        S2CPacketReceiver.register(GENERATE_CLIENT_DUMP, (client, handler, buf, responseSender) -> client.execute(() -> {
            Path path = Waila.GAME_DIR.resolve(".waila/WailaClientDump.md");
            if (DumpGenerator.generate(path) && client.player != null) {
                client.player.displayClientMessage(new TranslatableComponent("command.waila.client_dump_success", path), false);
            }
        }));
    }

    private static <T> void setBlackList(int[] ids, Set<T> set, Registry<T> registry) {
        for (int id : ids) {
            set.add(registry.byId(id));
        }
    }

    private static <T> void writeRawIds(FriendlyByteBuf buf, Set<T> set, Registry<T> registry) {
        buf.writeVarIntArray(set.stream().mapToInt(registry::getId).toArray());
    }

}
