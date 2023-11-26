package mcp.mobius.waila.network;

import java.util.stream.Collectors;

import lol.bai.badpackets.api.PacketSender;
import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet.ConfigC2S;
import mcp.mobius.waila.network.Packet.ConfigS2C;
import mcp.mobius.waila.network.Packet.PlayC2S;
import mcp.mobius.waila.network.Packet.PlayS2C;
import mcp.mobius.waila.network.common.VersionPayload;
import mcp.mobius.waila.network.common.c2s.VersionCommonC2SPacket;
import mcp.mobius.waila.network.config.s2c.BlacklistSyncConfigS2CPacket;
import mcp.mobius.waila.network.config.s2c.ConfigSyncConfigS2CPacket;
import mcp.mobius.waila.network.config.s2c.VersionConfigS2CPacket;
import mcp.mobius.waila.network.play.c2s.BlockDataRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.EntityDataRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.RawDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.TypedDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.s2c.GenerateClientDumpPlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.RawDataResponsePlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.TypedDataResponsePlayS2CPacket;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class Packets {

    public static void initServer() {
        // Common
        register(new VersionCommonC2SPacket());

        // Play
        register(new BlockDataRequestPlayC2SPacket());
        register(new EntityDataRequestPlayC2SPacket());
        register(new RawDataRequestContextPlayC2SPacket());
        register(new TypedDataRequestContextPlayC2SPacket());

        ConfigPackets.registerServerReadyCallback((handler, sender, server) ->
            sendS2CHandshakePackets(sender));

        // Older vanilla version doesn't have config stage and instead handshake happens on play stage
        PlayPackets.registerServerReadyCallback((handler, sender, server) -> {
            if (sender.canSend(VersionPayload.ID)) {
                sendS2CHandshakePackets(sender);
            }
        });
    }

    public static void initClient() {
        // Config
        register(new VersionConfigS2CPacket());
        register(new ConfigSyncConfigS2CPacket());
        register(new BlacklistSyncConfigS2CPacket());

        // Play
        register(new GenerateClientDumpPlayS2CPacket());
        register(new RawDataResponsePlayS2CPacket());
        register(new TypedDataResponsePlayS2CPacket());

        ConfigPackets.registerClientReadyCallback((handler, sender, client) ->
            sendVersionPacket(sender));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void register(Packet<?> packet) {
        if (packet instanceof ConfigC2S config) ConfigPackets.registerServerReceiver(config.id(), config, config);
        if (packet instanceof ConfigS2C config) ConfigPackets.registerClientReceiver(config.id(), config, config);

        if (packet instanceof PlayC2S play) PlayPackets.registerServerReceiver(play.id(), play, play);
        if (packet instanceof PlayS2C play) PlayPackets.registerClientReceiver(play.id(), play, play);
    }

    private static void sendVersionPacket(PacketSender sender) {
        sender.send(new VersionPayload(NETWORK_VERSION));
    }

    private static void sendS2CHandshakePackets(PacketSender sender) {
        sendVersionPacket(sender);

        var blacklistConfig = Waila.BLACKLIST_CONFIG.get();
        sender.send(new BlacklistSyncConfigS2CPacket.Payload(
            blacklistConfig.blocks, blacklistConfig.blockEntityTypes, blacklistConfig.entityTypes));

        sender.send(new ConfigSyncConfigS2CPacket.Payload(PluginConfig.getSyncableConfigs().stream()
            .collect(Collectors.toMap(ConfigEntry::getId, ConfigEntry::getLocalValue))));
    }

}
