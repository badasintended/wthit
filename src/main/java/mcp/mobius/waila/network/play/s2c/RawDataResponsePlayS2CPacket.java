package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class RawDataResponsePlayS2CPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("data"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG, Payload::data,
        Payload::new);

    @Override
    public void common() {
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> DataReader.CLIENT.reset(payload.data));
    }

    public record Payload(
        CompoundTag data
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
