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
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginDiscoverer;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaPlugin;
import net.minecraft.resources.ResourceLocation;

public abstract class DefaultPluginDiscoverer implements IPluginDiscoverer {

    public static final ResourceLocation LEGACY = Waila.id("legacy");

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

    protected Factory factory(String initializer) {
        return () -> (IWailaPlugin) Class.forName(initializer).getConstructor().newInstance();
    }

    protected void registerLegacy(String modId, String pluginId, IPluginInfo.Side side, List<String> requiredModIds, String initializer) {
        PluginInfo.register(LEGACY, modId, new ResourceLocation(pluginId), side, requiredModIds, true, factory(initializer));
    }

    protected void readPluginsJson(Candidates candidates, String modId, Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            var object = JsonParser.parseReader(reader).getAsJsonObject();

            for (var pluginId : object.keySet()) {
                var plugin = object.getAsJsonObject(pluginId);

                var initializer = plugin.getAsJsonPrimitive(KEY_INITIALIZER).getAsString();
                var side = plugin.has(KEY_SIDE)
                    ? Objects.requireNonNull(SIDES.get(plugin.get(KEY_SIDE).getAsString()), () -> readError(path) + ", invalid side, available: " + SIDES.keySet().stream().collect(Collectors.joining(", ", "[", "]")))
                    : IPluginInfo.Side.BOTH;

                List<String> required = new ArrayList<>();
                if (plugin.has(KEY_REQUIRED)) {
                    var array = plugin.getAsJsonArray(KEY_REQUIRED);
                    for (var element : array) {
                        var requiredModId = element.getAsString();
                        required.add(requiredModId);
                    }
                }

                var defaultEnabled = !plugin.has(KEY_DEFAULT_ENABLED) || plugin.get(KEY_DEFAULT_ENABLED).getAsBoolean();
                candidates.add(modId, new ResourceLocation(pluginId), side, required, defaultEnabled, factory(initializer));
            }
        } catch (IOException e) {
            throw new RuntimeException(readError(path), e);
        }
    }

    private static String readError(Path path) {
        return "Failed to read [" + path + "]";
    }

}
