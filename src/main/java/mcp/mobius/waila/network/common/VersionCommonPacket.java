package mcp.mobius.waila.network.common;

import java.util.function.Consumer;

import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class VersionCommonPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("version"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, Payload::version,
        Payload::new);

    @Override
    public void common() {
        ConfigPackets.registerServerChannel(TYPE, CODEC);
        ConfigPackets.registerClientChannel(TYPE, CODEC);
        PlayPackets.registerServerChannel(TYPE, CODEC);

        ConfigPackets.registerServerReceiver(TYPE, (context, payload) -> receive(context.handler()::disconnect, payload));
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> receive(context.handler()::disconnect, payload));
    }

    @Override
    public void client() {
        ConfigPackets.registerClientReceiver(TYPE, (context, payload) -> receive(context::disconnect, payload));
    }

    private void receive(Consumer<Component> disconnector, Payload payload) {
        if (payload.version() != NETWORK_VERSION) disconnector.accept(Component.literal(
            WailaConstants.MOD_NAME + " network version mismatch! " +
                "Server version is " + NETWORK_VERSION + " while client version is " + payload.version()));
    }

    public record Payload(
        int version
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
