package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class GenerateClientDumpPlayS2CPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("generate_client_dump"));
    public static final Payload PAYLOAD = new Payload();
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.unit(PAYLOAD);

    @Override
    public void common() {
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> {
            var client = context.client();
            var path = DumpGenerator.generate(DumpGenerator.CLIENT);
            if (path != null && client.player != null) {
                Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent.OpenFile(path.toString())));
                client.player.displayClientMessage(Component.translatable(Tl.Command.CLIENT_DUMP_SUCCESS, pathComponent), false);
            }
        });
    }

    public static class Payload implements CustomPacketPayload {

        private Payload() {
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
