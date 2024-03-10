package mcp.mobius.waila.network.common.s2c;

import java.util.List;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class PluginSyncCommonS2CPacket implements
    Packet.ConfigS2C<PluginSyncCommonS2CPacket.Payload>,
    Packet.PlayS2C<PluginSyncCommonS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("plugin");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(buf.readList(FriendlyByteBuf::readResourceLocation));
    }

    private static void receive(Payload payload) {
        PluginInfo.refresh();

        for (var plugin : payload.plugins) {
            ((PluginInfo) IPluginInfo.get(plugin)).setDisabledOnServer(true);
        }

        PluginLoader.INSTANCE.loadPlugins();
    }

    @Override
    public void receive(Minecraft client, ClientConfigurationPacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        receive(payload);
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        receive(payload);
    }

    public record Payload(
        List<ResourceLocation> plugins
    ) implements CustomPacketPayload {

        public Payload() {
            this(PluginInfo.getAll().stream()
                .filter(it -> !it.isEnabled())
                .map(IPluginInfo::getPluginId)
                .toList());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeCollection(plugins, FriendlyByteBuf::writeResourceLocation);
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }

    }

}
