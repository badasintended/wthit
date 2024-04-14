package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.common.s2c.BlacklistSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.ConfigSyncCommonS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ConfigSyncRequestPlayC2SPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("config_sync_req"));
    public static final Payload PAYLOAD = new Payload();
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.unit(PAYLOAD);

    @Override
    public void common() {
        PlayPackets.registerServerChannel(TYPE, CODEC);
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> {
            context.send(new BlacklistSyncCommonS2CPacket.Payload());
            context.send(new ConfigSyncCommonS2CPacket.Payload());
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
