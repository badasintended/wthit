package mcp.mobius.waila.paper;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_INT;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;
import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class PaperWaila extends JavaPlugin implements Listener, PluginMessageListener {

    static final String CHANNEL_VERSION = "waila:version";
    static final String CHANNEL_CONFIG = "waila:config";
    static final String CHANNEL_BLACKLIST = "waila:blacklist";

    static final String CHANNEL_BLOCK = "waila:block";
    static final String CHANNEL_ENTITY = "waila:entity";

    static final String CHANNEL_DATA_TYPED = "waila:data_typed";
    // Matches RawDataResponsePlayS2CPacket.TYPE.id() → Waila.id("data")
    static final String CHANNEL_DATA_RAW = "waila:data";

    // BadPackets channel sync protocol - required for Fabric clients using BadPackets
    static final String CHANNEL_BP_SYNC = "badpackets:channel_sync";
    static final byte BP_SYNC_INITIAL = 0x01;

    private PaperDataHandler dataHandler;

    @Override
    public void onEnable() {
        // ServiceLoader.load() uses the thread context classloader, which on Paper's plugin system
        // is not the plugin classloader. Swap it for the entire onEnable so that ServiceLoader can
        // find our services (ICommonService, PluginLoader, etc.) in the plugin JAR.
        var previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            dataHandler = new PaperDataHandler(this);

            Bukkit.getPluginManager().registerEvents(this, this);

            // BadPackets channel sync - register incoming to receive the client's response.
            // Outgoing sync is sent via NMS (bypasses Bukkit's channel check).
            Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_BP_SYNC, this);

            // Outgoing channels
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_VERSION);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_CONFIG);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_BLACKLIST);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_DATA_TYPED);
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_DATA_RAW);

            // Incoming channels
            Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_BLOCK, this);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, CHANNEL_ENTITY, this);

            // Initialize the WTHIT plugin system - loads data providers, populates PluginConfig
            // and Waila.BLACKLIST_CONFIG with registered values.
            PluginLoader.INSTANCE.loadPlugins();

            var plugins = PluginInfo.getAll();
            getLogger().info("[WTHIT] Loaded " + plugins.size() + " plugins:");
            for (var info : plugins) {
                getLogger().info("[WTHIT]   " + info.getPluginId() + " (enabled=" + info.isEnabled() + ")");
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Send BadPackets channel sync to the Fabric client so it registers waila:* channels.
        // BadPackets on the client only sends minecraft:register for its managed channels AFTER
        // receiving a badpackets:channel_sync INITIAL packet from the server.
        // Use a 1-tick delay to ensure the player's network handler is fully ready.
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (event.getPlayer().isOnline()) {
                sendBadPacketsChannelSync(event.getPlayer());
            }
        }, 1L);
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onPlayerRegisterChannelEvent(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        if (event.getChannel().equals(CHANNEL_VERSION)) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            writeVarInt(out, NETWORK_VERSION);
            player.sendPluginMessage(this, CHANNEL_VERSION, out.toByteArray());
        }

        if (event.getChannel().equals(CHANNEL_BLACKLIST)) {
            // Use WTHIT's own blacklist config (populated during loadPlugins) instead of a
            // custom config file. Wire format matches BlacklistSyncCommonS2CPacket.CODEC:
            // three flat string sets (blocks, blockEntityTypes, entityTypes).
            var blacklist = Waila.BLACKLIST_CONFIG.get();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            writeStringSet(out, blacklist.blocks);
            writeStringSet(out, blacklist.blockEntityTypes);
            writeStringSet(out, blacklist.entityTypes);
            player.sendPluginMessage(this, CHANNEL_BLACKLIST, out.toByteArray());
        }

        if (event.getChannel().equals(CHANNEL_CONFIG)) {
            // Use PluginConfig.getSyncableConfigs() to build the config packet, matching
            // exactly what the Fabric server does in ConfigSyncCommonS2CPacket.Payload().
            // This ensures the client receives proper server values for all synced configs
            // (e.g. featureConfig entries like wailax:item.enabled_block default to true).
            var syncableConfigs = PluginConfig.getSyncableConfigs().stream()
                .filter(it -> it.getOrigin().isEnabled())
                .collect(Collectors.toMap(ConfigEntry::getId, ConfigEntry::getLocalValue));

            var groups = syncableConfigs.keySet().stream()
                .collect(Collectors.groupingBy(Identifier::getNamespace));

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            writeVarInt(out, groups.size());
            groups.forEach((namespace, entries) -> {
                writeUtf(out, namespace);
                writeVarInt(out, entries.size());
                entries.forEach(e -> {
                    writeUtf(out, e.getPath());
                    var v = syncableConfigs.get(e);
                    if (v instanceof Boolean z) {
                        out.writeByte(CONFIG_BOOL);
                        out.writeBoolean(z);
                    } else if (v instanceof Integer i) {
                        out.writeByte(CONFIG_INT);
                        writeVarInt(out, i);
                    } else if (v instanceof Double d) {
                        out.writeByte(CONFIG_DOUBLE);
                        out.writeDouble(d);
                    } else if (v instanceof String str) {
                        out.writeByte(CONFIG_STRING);
                        writeUtf(out, str);
                    } else if (v instanceof Enum<?> en) {
                        out.writeByte(CONFIG_STRING);
                        writeUtf(out, en.name());
                    }
                });
            });
            player.sendPluginMessage(this, CHANNEL_CONFIG, out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        switch (channel) {
            case CHANNEL_BLOCK -> dataHandler.handleBlockRequest(player, message);
            case CHANNEL_ENTITY -> dataHandler.handleEntityRequest(player, message);
        }
    }

    /**
     * Sends a BadPackets channel_sync INITIAL packet to the client via NMS, advertising all waila:* channels.
     * This is required because BadPackets on the Fabric client only registers channels via
     * minecraft:register AFTER it receives this sync from the server.
     * <p>
     * Must bypass Bukkit's {@code sendPluginMessage} because Bukkit silently drops messages
     * to channels the client hasn't registered yet — but the whole point of this packet is to
     * trigger that registration.
     * <p>
     * Wire format: [byte: 0x01] [varint: namespace_count] [for each: utf namespace, varint path_count, [utf path...]]
     */
    private void sendBadPacketsChannelSync(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(BP_SYNC_INITIAL);

        // All channels are in the "waila" namespace
        writeVarInt(out, 1); // 1 namespace group
        writeUtf(out, "waila");
        writeVarInt(out, 7); // 7 channel paths
        writeUtf(out, "version");
        writeUtf(out, "config");
        writeUtf(out, "blacklist");
        writeUtf(out, "block");
        writeUtf(out, "entity");
        writeUtf(out, "data_typed");
        writeUtf(out, "data");

        // Send directly via NMS to bypass Bukkit's channel registration check
        var nmsPlayer = ((CraftPlayer) player).getHandle();
        var packet = new ClientboundCustomPayloadPacket(
            new DiscardedPayload(Identifier.parse(CHANNEL_BP_SYNC), out.toByteArray())
        );
        nmsPlayer.connection.send(packet);
    }

    static void writeVarInt(ByteArrayDataOutput out, int i) {
        while ((i & -128) != 0) {
            out.writeByte(i & 127 | 128);
            i >>>= 7;
        }
        out.writeByte(i);
    }

    static void writeUtf(ByteArrayDataOutput out, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 32767) {
            throw new RuntimeException("String too big (was " + bytes.length + " bytes encoded, max " + 32767 + ")");
        } else {
            writeVarInt(out, bytes.length);
            out.write(bytes);
        }
    }

    private static void writeStringSet(ByteArrayDataOutput out, Set<String> set) {
        writeVarInt(out, set.size());
        for (var s : set) {
            writeUtf(out, s);
        }
    }

}
