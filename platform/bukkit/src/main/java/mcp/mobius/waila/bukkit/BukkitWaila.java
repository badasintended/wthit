package mcp.mobius.waila.bukkit;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.mcless.config.ConfigIo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;
import static mcp.mobius.waila.mcless.network.NetworkConstants.NETWORK_VERSION;

public class BukkitWaila extends JavaPlugin implements Listener {

    private static final String CHANNEL_VERSION = "waila:version";
    private static final String CHANNEL_CONFIG = "waila:config";
    private static final String CHANNEL_BLACKLIST = "waila:blacklist";

    private final Consumer<String> warnLogger = msg -> getLogger().log(Level.WARNING, msg);
    private final BiConsumer<String, Throwable> errorLogger = (msg, t) -> getLogger().log(Level.SEVERE, msg, t);

    private final ConfigIo<Map<String, Map<String, JsonPrimitive>>> pluginConfigIo = new ConfigIo<>(
        warnLogger, errorLogger,
        new GsonBuilder().setPrettyPrinting().create(),
        new TypeToken<Map<String, Map<String, JsonPrimitive>>>() {
        }.getType(),
        LinkedHashMap::new);

    private final ConfigIo<BlacklistConfig> blacklistConfigIo = new ConfigIo<>(
        warnLogger, errorLogger,
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(NamespacedKey.class, new NamespacedKeySerializer())
            .create(),
        BlacklistConfig.class,
        BlacklistConfig::new);

    private Map<String, Map<String, JsonPrimitive>> pluginConfig;
    private BlacklistConfig blacklistConfig;

    @Override
    public void onLoad() {
        pluginConfig = pluginConfigIo.read(getDataFolder().toPath().resolve("waila_plugins.json"));
        blacklistConfig = blacklistConfigIo.read(getDataFolder().toPath().resolve("blacklist.json"));
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_VERSION);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_CONFIG);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, CHANNEL_BLACKLIST);
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
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            writeNamespacedKeys(out, blacklistConfig.blocks);
            writeNamespacedKeys(out, blacklistConfig.blockEntityTypes);
            writeNamespacedKeys(out, blacklistConfig.entityTypes);
            player.sendPluginMessage(this, CHANNEL_BLACKLIST, out.toByteArray());
        }

        if (event.getChannel().equals(CHANNEL_CONFIG)) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            writeVarInt(out, pluginConfig.size());
            pluginConfig.forEach((namespace, map) -> {
                writeUtf(out, namespace);
                writeVarInt(out, map.size());
                map.forEach((key, value) -> {
                    writeUtf(out, key);
                    if (value.isBoolean()) {
                        out.writeByte(CONFIG_BOOL);
                        out.writeBoolean(value.getAsBoolean());
                    } else if (value.isNumber()) {
                        out.writeByte(CONFIG_DOUBLE);
                        out.writeDouble(value.getAsDouble());
                    } else {
                        out.writeByte(CONFIG_STRING);
                        writeUtf(out, value.getAsString());
                    }
                });
            });
            player.sendPluginMessage(this, CHANNEL_CONFIG, out.toByteArray());
        }
    }

    private static void writeVarInt(ByteArrayDataOutput out, int i) {
        while ((i & -128) != 0) {
            out.writeByte(i & 127 | 128);
            i >>>= 7;
        }
        out.writeByte(i);
    }

    private static void writeUtf(ByteArrayDataOutput out, String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 32767) {
            throw new RuntimeException("String too big (was " + bytes.length + " bytes encoded, max " + 32767 + ")");
        } else {
            writeVarInt(out, bytes.length);
            out.write(bytes);
        }
    }

    private static void writeNamespacedKeys(ByteArrayDataOutput out, Set<NamespacedKey> keySet) {
        Map<String, List<NamespacedKey>> groups = keySet.stream().collect(Collectors.groupingBy(NamespacedKey::getNamespace));
        writeVarInt(out, groups.size());
        groups.forEach((namespace, keys) -> {
            writeUtf(out, namespace);
            writeVarInt(out, keys.size());
            keys.forEach(key -> writeUtf(out, key.getKey()));
        });
    }

}
