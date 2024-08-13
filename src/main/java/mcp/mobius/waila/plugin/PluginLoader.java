package mcp.mobius.waila.plugin;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.Internals;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.mcless.version.VersionRanges;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

public abstract class PluginLoader {

    public static final PluginLoader INSTANCE = Internals.loadService(PluginLoader.class);

    private static final Log LOG = Log.create();

    protected static final String[] PLUGIN_JSON_FILES = {
        "waila_plugins.json",
        "wthit_plugins.json"
    };

    protected static final String KEY_ENTRYPOINTS = "entrypoints";
    protected static final String KEY_ENTRYPOINT_COMMON = "common";
    protected static final String KEY_ENTRYPOINT_CLIENT = "client";
    protected static final String KEY_INITIALIZER = "initializer";
    protected static final String KEY_SIDE = "side";
    protected static final String KEY_REQUIRED = "required";
    protected static final String KEY_DEFAULT_ENABLED = "defaultEnabled";
    protected static final Map<String, PluginSide> SIDES = Map.of(
        "client", PluginSide.CLIENT,
        "server", PluginSide.DEDICATED_SERVER,
        "both", PluginSide.COMMON,
        "*", PluginSide.COMMON
    );

    private boolean gathered = false;

    public static void reloadServerPlugins(MinecraftServer server) {
        PluginInfo.saveToggleConfig();
        PluginInfo.refresh();
        INSTANCE.loadPlugins();

        server.getPlayerList().getPlayers().forEach(player -> {
            var sender = PacketSender.s2c(player);
            if (!sender.canSend(Packets.PLUGIN)) return;

            if (!server.isSingleplayerOwner(player.getGameProfile())) {
                var buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeCollection(PluginInfo.getAll().stream()
                    .filter(it -> !it.isEnabled())
                    .map(PluginInfo::getPluginId)
                    .toList(), FriendlyByteBuf::writeResourceLocation);
                sender.send(Packets.PLUGIN, buf);
            }

            Packets.sendConfig(sender);
        });
    }

    public static void reloadClientPlugins() {
        INSTANCE.loadPlugins();

        if (Minecraft.getInstance().getConnection() != null && PacketSender.c2s().canSend(Packets.CONFIG_SYNC_REQ)) {
            PacketSender.c2s().send(Packets.CONFIG_SYNC_REQ, new FriendlyByteBuf(Unpooled.buffer()));
        }
    }

    protected abstract void gatherPlugins();

    protected void readPluginsJson(String modId, Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            var object = JsonParser.parseReader(reader).getAsJsonObject();

            otherPlugin:
            for (var pluginId : object.keySet()) {
                var plugin = object.getAsJsonObject(pluginId);

                var deprecatedInit = plugin.has(KEY_INITIALIZER) ? plugin.getAsJsonPrimitive(KEY_INITIALIZER).getAsString() : null;
                var entrypoints = plugin.has(KEY_ENTRYPOINTS) ? plugin.getAsJsonObject(KEY_ENTRYPOINTS) : null;

                var side = plugin.has(KEY_SIDE)
                    ? Objects.requireNonNull(SIDES.get(plugin.get(KEY_SIDE).getAsString()), () -> readError(path) + ", invalid side, available: " + SIDES.keySet().stream().collect(Collectors.joining(", ", "[", "]")))
                    : PluginSide.COMMON;

                if (!side.matches(ICommonService.INSTANCE.getSide())) {
                    continue;
                }

                List<String> required = new ArrayList<>();
                if (plugin.has(KEY_REQUIRED)) {
                    var requiredElement = plugin.get(KEY_REQUIRED);

                    if (requiredElement.isJsonArray()) {
                        var array = requiredElement.getAsJsonArray();
                        for (var element : array) {
                            var requiredModId = element.getAsString();
                            if (ModInfo.get(requiredModId).isPresent()) {
                                required.add(requiredModId);
                            } else {
                                continue otherPlugin;
                            }
                        }
                    } else if (requiredElement.isJsonObject()) {
                        var requiredObj = requiredElement.getAsJsonObject();
                        for (var requiredModId : requiredObj.keySet()) {
                            var requiredMod = ModInfo.get(requiredModId);
                            var versionSpec = requiredObj.getAsJsonPrimitive(requiredModId).getAsString();
                            if (requiredMod.isPresent() && VersionRanges.parse(versionSpec).match(requiredMod.getVersion())) {
                                required.add(requiredModId);
                            } else {
                                continue otherPlugin;
                            }
                        }
                    }
                }

                var defaultEnabled = !plugin.has(KEY_DEFAULT_ENABLED) || plugin.get(KEY_DEFAULT_ENABLED).getAsBoolean();

                if (deprecatedInit != null) {
                    PluginInfo.registerDeprecated(modId, pluginId, side, deprecatedInit, required, defaultEnabled, false);
                } else if (entrypoints != null) {
                    var common = entrypoints.has(KEY_ENTRYPOINT_COMMON) ? entrypoints.getAsJsonPrimitive(KEY_ENTRYPOINT_COMMON).getAsString() : null;
                    var client = entrypoints.has(KEY_ENTRYPOINT_CLIENT) ? entrypoints.getAsJsonPrimitive(KEY_ENTRYPOINT_CLIENT).getAsString() : null;

                    if (common == null && client == null) {
                        throw new NullPointerException(readError(path) + ", need at least one entrypoint");
                    }

                    PluginInfo.register(modId, pluginId, side, common, client, required, defaultEnabled);
                } else {
                    throw new NullPointerException(readError(path) + ", need at least one entrypoint");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(readError(path), e);
        }
    }

    public final void loadPlugins() {
        Registrar.destroy();

        if (!gathered) {
            gathered = true;
            gatherPlugins();

            if (Waila.DEBUG_CONFIG.get().showTestPluginToggle) {
                PluginInfo.registerDeprecated(WailaConstants.MOD_ID, Waila.id("test").toString(), PluginSide.COMMON, "mcp.mobius.waila.plugin.test.WailaPluginTest", List.of(), false, false);
            }
        }

        PluginInfo.saveToggleConfig();
        PluginInfo extraPlugin = null;

        // TODO: remove legacy method on Minecraft 1.21
        List<String> legacyPlugins = new ArrayList<>();
        for (var info : PluginInfo.getAll()) {
            if (info.getPluginId().equals(Waila.id("extra"))) {
                extraPlugin = info;
            } else {
                initialize(info);
            }

            if (info.isLegacy()) {
                legacyPlugins.add(info.getPluginId().toString());
            }
        }

        if (extraPlugin != null) initialize(extraPlugin);

        if (Waila.DEV && !legacyPlugins.isEmpty()) {
            LOG.warn("Found plugins registered via legacy platform-dependant method:");
            LOG.warn(legacyPlugins.stream().collect(Collectors.joining(", ", "[", "]")));
            LOG.warn("The method will be removed on Minecraft 1.21");
        }

        Registrar.get().lock();
        PluginConfig.reload();

        JsonConfig.reloadAllInstances();
    }

    private void initialize(PluginInfo info) {
        Registrar.get().attach(info);

        var id = info.getPluginId();

        var deprecatedInit = info.getDeprecatedInit();
        var common = info.getCommon();
        var client = info.getClient();

        if (info.isEnabled()) {
            if (deprecatedInit != null) LOG.info("Initializing plugin {} at {}", id, deprecatedInit.getClass().getCanonicalName());
            if (common != null) LOG.info("Initializing common plugin {} at {}", id, common.getClass().getCanonicalName());
            if (client != null) LOG.info("Initializing client plugin {} at {}", id, client.getClass().getCanonicalName());
        } else {
            LOG.info("Skipping disabled plugin {}", id);
        }

        if (deprecatedInit != null) deprecatedInit.register(Registrar.get());
        if (common != null) common.register(Registrar.get());
        if (client != null) client.register(Registrar.get());

        Registrar.get().attach(null);
    }

    private static String readError(Path path) {
        return "Failed to read [" + path + "]";
    }

}
