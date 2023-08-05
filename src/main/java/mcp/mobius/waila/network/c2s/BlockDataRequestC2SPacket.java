package mcp.mobius.waila.network.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.s2c.RawDataResponseS2CPacket;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockDataRequestC2SPacket implements Packet.C2S<BlockDataRequestC2SPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("block");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(buf.readBlockHitResult());
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        BlockHitResult hitResult = payload.hitResult;

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
            DataWriter.INSTANCE.tryAppendData(provider, accessor);
        }

        for (IDataProvider<BlockEntity> provider : registrar.blockData.get(state.getBlock())) {
            DataWriter.INSTANCE.tryAppendData(provider, accessor);
        }

        raw.putInt("x", pos.getX());
        raw.putInt("y", pos.getY());
        raw.putInt("z", pos.getZ());
        //noinspection ConstantConditions
        raw.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());
        raw.putLong("WailaTime", System.currentTimeMillis());

        responseSender.send(new RawDataResponseS2CPacket.Payload(raw));
        DataWriter.INSTANCE.sendTypedPackets(responseSender, player);
    }

    public record Payload(
        BlockHitResult hitResult
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBlockHitResult(hitResult);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
