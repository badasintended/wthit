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
import mcp.mobius.waila.api.IPluginInfo;
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

    protected static final String KEY_INITIALIZER = "initializer";
    protected static final String KEY_SIDE = "side";
    protected static final String KEY_REQUIRED = "required";
    protected static final String KEY_DEFAULT_ENABLED = "defaultEnabled";
    protected static final Map<String, IPluginInfo.Side> SIDES = Map.of(
        "client", IPluginInfo.Side.CLIENT,
        "server", IPluginInfo.Side.SERVER,
        "both", IPluginInfo.Side.BOTH,
        "*", IPluginInfo.Side.BOTH
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
                    .map(IPluginInfo::getPluginId)
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

                var initializer = plugin.getAsJsonPrimitive(KEY_INITIALIZER).getAsString();
                var side = plugin.has(KEY_SIDE)
                    ? Objects.requireNonNull(SIDES.get(plugin.get(KEY_SIDE).getAsString()), () -> readError(path) + ", invalid side, available: " + SIDES.keySet().stream().collect(Collectors.joining(", ", "[", "]")))
                    : IPluginInfo.Side.BOTH;

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

                PluginInfo.register(modId, pluginId, side, initializer, required, defaultEnabled, false);
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
        }

        PluginInfo.saveToggleConfig();
        IPluginInfo extraPlugin = null;

        // TODO: remove legacy method on Minecraft 1.21
        List<String> legacyPlugins = new ArrayList<>();
        for (var info : PluginInfo.getAll()) {
            if (info.getPluginId().equals(Waila.id("extra"))) {
                extraPlugin = info;
            } else {
                initialize(info);
            }

            if (((PluginInfo) info).isLegacy()) {
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

        JsonConfig.INSTANCES.forEach(it -> it.write(it.get(), true));
    }

    private void initialize(IPluginInfo info) {
        Registrar.get().attach(info);

        if (info.isEnabled()) {
            LOG.info("Initializing plugin {} at {}", info.getPluginId(), info.getInitializer().getClass().getCanonicalName());
        } else {
            LOG.info("Skipping disabled plugin {}", info.getPluginId());
        }

        info.getInitializer().register(Registrar.get());
        Registrar.get().attach(null);
    }

    private static String readError(Path path) {
        return "Failed to read [" + path + "]";
    }

}
