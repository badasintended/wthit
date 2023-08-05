package mcp.mobius.waila.network;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class VersionPacket implements Packet.Common<VersionPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("version");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(buf.readVarInt());
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        if (payload.version != NETWORK_VERSION) {
            handler.disconnect(Component.literal(
                WailaConstants.MOD_NAME + " network version mismatch! " +
                    "Server version is " + NETWORK_VERSION + " while client version is " + payload.version));
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        if (payload.version != NETWORK_VERSION) {
            handler.getConnection().disconnect(Component.literal(
                WailaConstants.MOD_NAME + " network version mismatch! " +
                    "Server version is " + payload.version + " while client version is " + NETWORK_VERSION));
        }
    }

    public record Payload(
        int version
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(version);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
