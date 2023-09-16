package mcp.mobius.waila.network.config.s2c;

import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.config.ConfigPackets;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.common.VersionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class VersionConfigS2CPacket implements Packet.ConfigS2C<VersionPayload> {

    @Override
    public ResourceLocation id() {
        return VersionPayload.ID;
    }

    @Override
    public VersionPayload read(FriendlyByteBuf buf) {
        return new VersionPayload(buf.readVarInt());
    }

    private Component receive(VersionPayload payload) {
        return payload.version() != NETWORK_VERSION
            ? Component.literal(WailaConstants.MOD_NAME + " network version mismatch! Server version is " + payload.version() + " while client version is " + NETWORK_VERSION)
            : null;
    }

    @Override
    public void receive(Minecraft client, ClientConfigurationPacketListenerImpl handler, VersionPayload payload, PacketSender responseSender) {
        var disconnectReason = receive(payload);

        if (disconnectReason != null) {
            ConfigPackets.disconnect(handler, disconnectReason);
        }
    }

}
