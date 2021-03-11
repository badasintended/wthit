package mcp.mobius.waila.network;

import java.util.function.Supplier;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class MessageRequestTile {

    public BlockPos pos;

    public MessageRequestTile(BlockEntity tile) {
        this.pos = tile.getPos();
    }

    private MessageRequestTile(BlockPos pos) {
        this.pos = pos;
    }

    public static MessageRequestTile read(PacketByteBuf buffer) {
        return new MessageRequestTile(BlockPos.fromLong(buffer.readLong()));
    }

    public static void write(MessageRequestTile message, PacketByteBuf buffer) {
        buffer.writeLong(message.pos.asLong());
    }

    public static class Handler {

        public static void onMessage(MessageRequestTile message, Supplier<NetworkEvent.Context> context) {
            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null)
                return;

            server.execute(() -> {
                ServerPlayerEntity player = context.get().getSender();
                World world = player.world;
                if (!world.isChunkLoaded(message.pos))
                    return;

                BlockEntity tile = world.getBlockEntity(message.pos);
                BlockState state = world.getBlockState(message.pos);

                if (tile == null)
                    return;

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTProviders(tile) || WailaRegistrar.INSTANCE.hasNBTProviders(state.getBlock())) {
                    WailaRegistrar.INSTANCE.getNBTProviders(tile).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, tile)));
                    WailaRegistrar.INSTANCE.getNBTProviders(state.getBlock()).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, tile)));
                } else {
                    tile.toTag(tag);
                }

                tag.putInt("x", message.pos.getX());
                tag.putInt("y", message.pos.getY());
                tag.putInt("z", message.pos.getZ());
                tag.putString("id", tile.getType().getRegistryName().toString());

                Waila.NETWORK.sendTo(new MessageReceiveData(tag), player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT);
            });
            context.get().setPacketHandled(true);
        }

    }

}
