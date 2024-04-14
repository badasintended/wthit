package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class RawDataRequestContextPlayC2SPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("ctx"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, Payload::ctx,
        Payload::new);

    @Override
    public void common() {
        PlayPackets.registerServerChannel(TYPE, CODEC);
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> DataReader.SERVER.reset(payload.ctx));
    }

    public record Payload(
        CompoundTag ctx
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
