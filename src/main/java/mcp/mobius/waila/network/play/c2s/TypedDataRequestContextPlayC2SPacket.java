package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataType;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class TypedDataRequestContextPlayC2SPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("ctx_typed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        DataType.CODEC, Payload::ctx,
        Payload::new);

    @Override
    public void common() {
        PlayPackets.registerServerChannel(TYPE, CODEC);
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> DataReader.SERVER.add(payload.ctx));
    }

    public record Payload(
        IData ctx
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
