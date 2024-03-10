package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockDataRequestPlayC2SPacket implements Packet.PlayC2S<BlockDataRequestPlayC2SPacket.Payload> {

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
        var hitResult = payload.hitResult;

        var registrar = Registrar.get();
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
            DataWriter.SERVER.tryAppend(player, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
        }

        for (var provider : registrar.blockData.get(state.getBlock())) {
            DataWriter.SERVER.tryAppend(player, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
        }

        raw.putInt("x", pos.getX());
        raw.putInt("y", pos.getY());
        raw.putInt("z", pos.getZ());
        //noinspection ConstantConditions
        raw.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());
        raw.putLong("WailaTime", System.currentTimeMillis());

        DataWriter.SERVER.send(responseSender, player);
        DataReader.SERVER.reset(null);
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
