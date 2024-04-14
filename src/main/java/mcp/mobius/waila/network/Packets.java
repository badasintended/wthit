package mcp.mobius.waila.network;

import java.util.List;

import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.network.common.VersionCommonPacket;
import mcp.mobius.waila.network.common.s2c.BlacklistSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.ConfigSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.PluginSyncCommonS2CPacket;
import mcp.mobius.waila.network.play.c2s.BlockDataRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.ConfigSyncRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.EntityDataRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.RawDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.TypedDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.s2c.GenerateClientDumpPlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.RawDataResponsePlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.TypedDataResponsePlayS2CPacket;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class Packets {

    private static final List<Packet> PACKETS = List.of(
        new VersionCommonPacket(),

        new BlacklistSyncCommonS2CPacket(),
        new ConfigSyncCommonS2CPacket(),
        new PluginSyncCommonS2CPacket(),

        new BlockDataRequestPlayC2SPacket(),
        new ConfigSyncRequestPlayC2SPacket(),
        new EntityDataRequestPlayC2SPacket(),
        new RawDataRequestContextPlayC2SPacket(),
        new TypedDataRequestContextPlayC2SPacket(),

        new GenerateClientDumpPlayS2CPacket(),
        new RawDataResponsePlayS2CPacket(),
        new TypedDataResponsePlayS2CPacket()
    );

    public static void initServer() {
        PACKETS.forEach(Packet::common);

        ConfigPackets.registerServerReadyCallback(Packets::sendS2CHandshakePackets);

        // Older vanilla version doesn't have config stage and instead handshake happens on play stage
        PlayPackets.registerServerReadyCallback(context -> {
            if (context.canSend(VersionCommonPacket.TYPE)) {
                sendS2CHandshakePackets(context);
            }
        });
    }

    public static void initClient() {
        PACKETS.forEach(Packet::client);

        ConfigPackets.registerClientReadyCallback(Packets::sendVersionPacket);
    }

    private static void sendVersionPacket(PacketSender sender) {
        sender.send(new VersionCommonPacket.Payload(NETWORK_VERSION));
    }

    private static void sendS2CHandshakePackets(PacketSender sender) {
        sendVersionPacket(sender);

        sender.send(new PluginSyncCommonS2CPacket.Payload());
        sender.send(new BlacklistSyncCommonS2CPacket.Payload());
        sender.send(new ConfigSyncCommonS2CPacket.Payload());
    }

}
