package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.play.PlayPackets;
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class BlockDataRequestPlayC2SPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("block"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.ofMember(
        (p, buf) -> buf.writeBlockHitResult(p.hitResult),
        buf -> new Payload(buf.readBlockHitResult()));

    @Override
    public void common() {
        PlayPackets.registerServerChannel(TYPE, CODEC);
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> {
            var player = context.player();
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

            DataWriter.SERVER.send(context, player);
            DataReader.SERVER.reset(null);
        });
    }

    public record Payload(
        BlockHitResult hitResult
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
