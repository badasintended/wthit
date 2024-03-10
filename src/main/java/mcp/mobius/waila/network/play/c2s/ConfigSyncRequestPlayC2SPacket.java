package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.common.s2c.BlacklistSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.ConfigSyncCommonS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ConfigSyncRequestPlayC2SPacket implements Packet.PlayC2S<ConfigSyncRequestPlayC2SPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("config_sync_req");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        responseSender.send(new BlacklistSyncCommonS2CPacket.Payload());
        responseSender.send(new ConfigSyncCommonS2CPacket.Payload());
    }

    public record Payload() implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

    }

}
