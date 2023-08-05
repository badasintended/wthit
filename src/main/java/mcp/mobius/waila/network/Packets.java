package mcp.mobius.waila.network;

import java.util.Set;

import lol.bai.badpackets.api.event.PacketSenderReadyCallback;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.network.c2s.BlockDataRequestC2SPacket;
import mcp.mobius.waila.network.c2s.EntityDataRequestC2SPacket;
import mcp.mobius.waila.network.s2c.BlacklistSyncS2CPacket;
import mcp.mobius.waila.network.s2c.ConfigSyncS2CPacket;
import mcp.mobius.waila.network.s2c.GenerateClientDumpS2CPacket;
import mcp.mobius.waila.network.s2c.RawDataResponseS2CPacket;
import mcp.mobius.waila.network.s2c.TypedDataResponseS2CPacket;

import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class Packets {

    private static final Set<Packet<?>> PACKETS = Set.of(
        new VersionPacket(),

        new BlockDataRequestC2SPacket(),
        new EntityDataRequestC2SPacket(),

        new BlacklistSyncS2CPacket(),
        new ConfigSyncS2CPacket(),
        new GenerateClientDumpS2CPacket(),
        new RawDataResponseS2CPacket(),
        new TypedDataResponseS2CPacket());

    public static void initServer() {
        PACKETS.stream()
            .filter(it -> it instanceof Packet.C2S<?>)
            .forEach(Packet::register);

        PacketSenderReadyCallback.registerServer((handler, sender, server) -> {
            sender.send(new VersionPacket.Payload(NETWORK_VERSION));

            BlacklistConfig blacklistConfig = Waila.BLACKLIST_CONFIG.get();
            sender.send(new BlacklistSyncS2CPacket.Payload(blacklistConfig.blockIds, blacklistConfig.blockEntityTypeIds, blacklistConfig.entityTypeIds));

            sender.send(new ConfigSyncS2CPacket.Payload(null));
        });
    }

    public static void initClient() {
        PACKETS.stream()
            .filter(it -> it instanceof Packet.S2C<?>)
            .forEach(Packet::register);

        PacketSenderReadyCallback.registerClient((handler, sender, client) ->
            sender.send(new VersionPacket.Payload(NETWORK_VERSION)));
    }

}
