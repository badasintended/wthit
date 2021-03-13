package mcp.mobius.waila.network.forge;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.network.NetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.network.forge.ClientNetworkHandlerImpl.ReceiveData;

public class NetworkHandlerImpl extends NetworkHandler {

    private static final String PROTOCOL = "1.0.0";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        new Identifier(Waila.MODID, "networking"),
        () -> PROTOCOL, p -> true, p -> true
    );

    public static void init() {
        int i = 0;
        NETWORK.registerMessage(i++, ReceiveData.class,
            (msg, buf) -> buf.writeCompoundTag(msg.tag),
            (buf) -> new ReceiveData(buf.readCompoundTag()),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> DataAccessor.INSTANCE.setServerData(msg.tag));
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, SendConfig.class,
            (msg, buf) -> {
                buf.writeInt(msg.forcedKeys.size());
                msg.forcedKeys.forEach((k, v) -> {
                    buf.writeInt(k.toString().length());
                    buf.writeString(k.toString());
                    buf.writeBoolean(v);
                });
            },
            (buf) -> {
                int size = buf.readInt();
                Map<Identifier, Boolean> temp = Maps.newHashMap();
                for (int j = 0; j < size; j++) {
                    int idLength = buf.readInt();
                    Identifier id = new Identifier(buf.readString(idLength));
                    boolean value = buf.readBoolean();
                    temp.put(id, value);
                }

                return new SendConfig(temp);
            },
            (message, context) -> {
                context.get().enqueueWork(() -> {
                    message.forcedKeys.forEach(PluginConfig.INSTANCE::set);
                    Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(message.forcedKeys));
                });
                context.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestEntity.class,
            (msg, buf) -> buf.writeInt(msg.entityId),
            (buf) -> new RequestEntity(buf.readInt()),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    World world = player.world;
                    Entity entity = world.getEntityById(msg.entityId);

                    if (entity == null)
                        return;

                    CompoundTag tag = new CompoundTag();
                    if (WailaRegistrar.INSTANCE.hasNBTEntityProviders(entity)) {
                        WailaRegistrar.INSTANCE.getNBTEntityProviders(entity).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, (LivingEntity) entity)));
                    } else {
                        entity.toTag(tag);
                    }

                    tag.putInt("WailaEntityID", entity.getEntityId());

                    NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag));
                });
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestBlockEntity.class,
            (msg, buf) -> buf.writeBlockPos(msg.pos),
            (buf) -> new RequestBlockEntity(buf.readBlockPos()),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    World world = player.world;
                    if (!world.isChunkLoaded(msg.pos))
                        return;

                    BlockEntity be = world.getBlockEntity(msg.pos);
                    BlockState state = world.getBlockState(msg.pos);

                    if (be == null)
                        return;

                    CompoundTag tag = new CompoundTag();
                    if (WailaRegistrar.INSTANCE.hasNBTProviders(be) || WailaRegistrar.INSTANCE.hasNBTProviders(state.getBlock())) {
                        WailaRegistrar.INSTANCE.getNBTProviders(be).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, be)));
                        WailaRegistrar.INSTANCE.getNBTProviders(state.getBlock()).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, be)));
                    } else {
                        be.toTag(tag);
                    }

                    tag.putInt("x", msg.pos.getX());
                    tag.putInt("y", msg.pos.getY());
                    tag.putInt("z", msg.pos.getZ());
                    tag.putString("id", be.getType().getRegistryName().toString());

                    NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag));
                });
                ctx.get().setPacketHandled(true);
            }
        );
    }

    public static void requestEntity(Entity entity) {
        NETWORK.sendToServer(new RequestEntity(entity.getEntityId()));
    }

    public static void requestTile(BlockEntity blockEntity) {
        NETWORK.sendToServer(new RequestBlockEntity(blockEntity.getPos()));
    }

    public static void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SendConfig(config));
    }

    public static class SendConfig {

        Map<Identifier, Boolean> forcedKeys = Maps.newHashMap();

        SendConfig(@Nullable Map<Identifier, Boolean> forcedKeys) {
            this.forcedKeys = forcedKeys;
        }

        SendConfig(PluginConfig config) {
            Set<ConfigEntry> entries = config.getSyncableConfigs();
            entries.forEach(e -> forcedKeys.put(e.getId(), e.getValue()));
        }

    }

    public static class RequestEntity {

        int entityId;

        RequestEntity(int entityId) {
            this.entityId = entityId;
        }

    }

    public static class RequestBlockEntity {

        BlockPos pos;

        RequestBlockEntity(BlockPos pos) {
            this.pos = pos;
        }

    }

}
