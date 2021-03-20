package mcp.mobius.waila.fabric;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.network.NetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FabricNetworkHandler extends NetworkHandler {

    static final Identifier REQUEST_ENTITY = new Identifier(Waila.MODID, "request_entity");
    static final Identifier REQUEST_BLOCK = new Identifier(Waila.MODID, "request_tile");
    static final Identifier RECEIVE_DATA = new Identifier(Waila.MODID, "receive_data");
    static final Identifier GET_CONFIG = new Identifier(Waila.MODID, "send_config");

    @Override
    public void main() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ENTITY, (server, player, handler, packetByteBuf, sender) -> {
            World world = player.world;
            Entity entity = world.getEntityById(packetByteBuf.readInt());
            server.execute(() -> {
                if (entity == null)
                    return;

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTEntityProviders(entity)) {
                    WailaRegistrar.INSTANCE.getNBTEntityProviders(entity).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, (LivingEntity) entity)));
                } else {
                    entity.toTag(tag);
                }

                tag.putInt("WailaEntityID", entity.getEntityId());

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeCompoundTag(tag);
                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(RECEIVE_DATA, buf));
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BLOCK, (server, player, handler, packetByteBuf, sender) -> {
            World world = player.world;
            BlockPos pos = packetByteBuf.readBlockPos();

            server.execute(() -> {
                if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4))
                    return;

                BlockEntity tile = world.getBlockEntity(pos);
                if (tile == null)
                    return;

                BlockState state = world.getBlockState(pos);

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTProviders(tile) || WailaRegistrar.INSTANCE.hasNBTProviders(state.getBlock())) {
                    WailaRegistrar.INSTANCE.getNBTProviders(tile).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, tile)));
                    WailaRegistrar.INSTANCE.getNBTProviders(state.getBlock()).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, tile)));
                } else {
                    tile.toTag(tag);
                }

                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
                tag.putString("id", Registry.BLOCK_ENTITY_TYPE.getId(tile.getType()).toString());

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeCompoundTag(tag);
                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(RECEIVE_DATA, buf));
            });
        });
    }

    @Override
    public void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        Waila.LOGGER.info("Sending config to {} ({})", player.getGameProfile().getName(), player.getGameProfile().getId());
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Set<ConfigEntry> entries = config.getSyncableConfigs();
        buf.writeInt(entries.size());
        entries.forEach(e -> {
            buf.writeInt(e.getId().toString().length());
            buf.writeString(e.getId().toString());
            buf.writeBoolean(e.getValue());
        });

        ServerPlayNetworking.send(player, GET_CONFIG, buf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void client() {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_DATA, (client, handler, buf, responseSender) -> {
            CompoundTag tag = buf.readCompoundTag();
            client.execute(() -> DataAccessor.INSTANCE.setServerData(tag));
        });

        ClientPlayNetworking.registerGlobalReceiver(GET_CONFIG, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            Map<Identifier, Boolean> temp = Maps.newHashMap();
            for (int i = 0; i < size; i++) {
                int idLength = buf.readInt();
                Identifier id = new Identifier(buf.readString(idLength));
                boolean value = buf.readBoolean();
                temp.put(id, value);
            }

            client.execute(() -> {
                temp.forEach(PluginConfig.INSTANCE::set);
                Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(temp));
            });
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestEntity(Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getEntityId());
        ClientPlayNetworking.send(REQUEST_ENTITY, buf);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestBlock(BlockEntity blockEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(blockEntity.getPos());
        ClientPlayNetworking.send(REQUEST_BLOCK, buf);
    }

}
