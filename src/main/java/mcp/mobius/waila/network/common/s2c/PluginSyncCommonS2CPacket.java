package mcp.mobius.waila.network.common.s2c;

import java.util.ArrayList;
import java.util.List;

import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class PluginSyncCommonS2CPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("plugin"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, Identifier.STREAM_CODEC), Payload::plugins,
        Payload::new);

    @Override
    public void common() {
        ConfigPackets.registerClientChannel(TYPE, CODEC);
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        ConfigPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
    }

    private static void receive(Payload payload) {
        PluginInfo.refresh();

        for (var id : payload.plugins) {
            var plugin = PluginInfo.get(id);
            if (plugin != null) plugin.setDisabledOnServer(true);
        }

        PluginLoader.INSTANCE.loadPlugins();
    }

    public record Payload(
        List<Identifier> plugins
    ) implements CustomPacketPayload {

        public Payload() {
            this(PluginInfo.getAll().stream()
                .filter(it -> !it.isEnabled())
                .map(PluginInfo::getPluginId)
                .toList());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
