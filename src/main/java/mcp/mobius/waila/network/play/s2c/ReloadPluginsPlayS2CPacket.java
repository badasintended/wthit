package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ReloadPluginsPlayS2CPacket implements Packet.PlayS2C<ReloadPluginsPlayS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("reload_plugins");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload();
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        PluginLoader.INSTANCE.loadPlugins();
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
