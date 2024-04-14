package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataType;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.network.Packet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class TypedDataResponsePlayS2CPacket implements Packet {

    public static final ResourceLocation ID = Waila.id("data_typed");
    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        DataType.CODEC, Payload::data,
        Payload::new);

    @Override
    public void common() {
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> DataReader.CLIENT.add(payload.data));
    }

    public record Payload(
        IData data
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
