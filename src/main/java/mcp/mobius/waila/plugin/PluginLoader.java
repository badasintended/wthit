package mcp.mobius.waila.plugin;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonParser;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.Internals;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.common.s2c.BlacklistSyncCommonS2CPacket;
import mcp.mobius.waila.network.common.s2c.ConfigSyncCommonS2CPacket;
import mcp.mobius.waila.network.play.c2s.ConfigSyncRequestPlayC2SPacket;
import mcp.mobius.waila.network.play.s2c.ReloadPluginsPlayS2CPacket;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public abstract class PluginLoader {

    public static final PluginLoader INSTANCE = Internals.loadService(PluginLoader.class);

    private static final Log LOG = Log.create();
    private static final boolean ENABLE_TEST_PLUGIN = Boolean.getBoolean("waila.enableTestPlugin");

    protected static final String[] PLUGIN_JSON_FILES = {
        "waila_plugins.json",
        "wthit_plugins.json"
    };

    protected static final String KEY_INITIALIZER = "initializer";
    protected static final String KEY_SIDE = "side";
    protected static final String KEY_REQUIRED = "required";
    protected static final Map<String, IPluginInfo.Side> SIDES = Map.of(
        "client", IPluginInfo.Side.CLIENT,
        "server", IPluginInfo.Side.SERVER,
        "both", IPluginInfo.Side.BOTH,
        "*", IPluginInfo.Side.BOTH
    );

    public static void reloadServerPlugins(MinecraftServer server) {
        INSTANCE.loadPlugins();
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.reload();

        server.getPlayerList().getPlayers().forEach(player -> {
            var sender = PacketSender.s2c(player);
            if (!sender.canSend(ReloadPluginsPlayS2CPacket.ID)) return;

            if (!server.isSingleplayerOwner(player.getGameProfile())) {
                sender.send(new ReloadPluginsPlayS2CPacket.Payload());
            }

            sender.send(new BlacklistSyncCommonS2CPacket.Payload());
            sender.send(new ConfigSyncCommonS2CPacket.Payload());
        });
    }

    public static void reloadClientPlugins() {
        INSTANCE.loadPlugins();
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.reload();

        if (Minecraft.getInstance().getConnection() != null && PacketSender.c2s().canSend(ConfigSyncRequestPlayC2SPacket.ID)) {
            PacketSender.c2s().send(new ConfigSyncRequestPlayC2SPacket.Payload());
        }
    }

    protected abstract void gatherPlugins();

    protected void readPluginsJson(String modId, Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            var object = JsonParser.parseReader(reader).getAsJsonObject();

            outer:
            for (var pluginId : object.keySet()) {
                var plugin = object.getAsJsonObject(pluginId);

                var initializer = plugin.getAsJsonPrimitive(KEY_INITIALIZER).getAsString();
                var side = plugin.has(KEY_SIDE)
                    ? Objects.requireNonNull(SIDES.get(plugin.get(KEY_SIDE).getAsString()), () -> readError(path) + ", invalid side, available: " + SIDES.keySet().stream().collect(Collectors.joining(", ", "[", "]")))
                    : IPluginInfo.Side.BOTH;

                if (!side.matches(ICommonService.INSTANCE.getSide())) {
                    break;
                }

                List<String> required = new ArrayList<>();
                if (plugin.has(KEY_REQUIRED)) {
                    var array = plugin.getAsJsonArray(KEY_REQUIRED);
                    for (var element : array) {
                        var requiredModId = element.getAsString();
                        if (ModInfo.get(requiredModId).isPresent()) {
                            required.add(requiredModId);
                        } else {
                            break outer;
                        }
                    }
                }

                PluginInfo.register(modId, pluginId, side, initializer, required, false);
            }
        } catch (IOException e) {
            throw new RuntimeException(readError(path), e);
        }
    }

    public final void loadPlugins() {
        Registrar.destroy();
        PluginInfo.clear();
        gatherPlugins();

        if (ENABLE_TEST_PLUGIN) {
            PluginInfo.register(WailaConstants.MOD_ID, "waila:test", IPluginInfo.Side.BOTH, "mcp.mobius.waila.plugin.test.WailaTest", Collections.emptyList(), false);
        }

        IPluginInfo extraPlugin = null;

        // TODO: remove legacy method on Minecraft 1.21
        List<String> legacyPlugins = new ArrayList<>();
        for (var info : PluginInfo.getAll()) {
            if (info.getPluginId().equals(Waila.id("extra"))) {
                extraPlugin = info;
            } else {
                register(info);
            }

            if (((PluginInfo) info).isLegacy()) {
                legacyPlugins.add(info.getPluginId().toString());
            }
        }

        if (extraPlugin != null) register(extraPlugin);

        if (!legacyPlugins.isEmpty()) {
            LOG.warn("Found plugins registered via legacy platform-dependant method:");
            LOG.warn(legacyPlugins.stream().collect(Collectors.joining(", ", "[", "]")));
            LOG.warn("The method will be removed on Minecraft 1.21");
        }

        Registrar.get().lock();
        PluginConfig.reload();
    }

    private void register(IPluginInfo info) {
        LOG.info("Registering plugin {} at {}", info.getPluginId(), info.getInitializer().getClass().getCanonicalName());
        info.getInitializer().register(Registrar.get());
    }

    private static String readError(Path path) {
        return "Failed to read [" + path + "]";
    }

}
