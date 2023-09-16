package mcp.mobius.waila.network.common.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.common.VersionPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class VersionCommonC2SPacket implements
    Packet.ConfigC2S<VersionPayload>,
    Packet.PlayC2S<VersionPayload> {

    @Override
    public ResourceLocation id() {
        return VersionPayload.ID;
    }

    @Override
    public VersionPayload read(FriendlyByteBuf buf) {
        return new VersionPayload(buf.readVarInt());
    }

    private void receive(ServerCommonPacketListenerImpl handler, VersionPayload payload) {
        if (payload.version() != NETWORK_VERSION) handler.disconnect(Component.literal(
            WailaConstants.MOD_NAME + " network version mismatch! " +
            "Server version is " + NETWORK_VERSION + " while client version is " + payload.version()));
    }

    @Override
    public void receive(MinecraftServer server, ServerConfigurationPacketListenerImpl handler, VersionPayload payload, PacketSender responseSender, TaskFinisher taskFinisher) {
        receive(handler, payload);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, VersionPayload payload, PacketSender responseSender) {
        receive(handler, payload);
    }


}
