package mcp.mobius.waila.fabric;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import mcp.mobius.waila.plugin.PluginSide;
import mcp.mobius.waila.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import static net.fabricmc.loader.api.metadata.CustomValue.CvType.ARRAY;
import static net.fabricmc.loader.api.metadata.CustomValue.CvType.OBJECT;
import static net.fabricmc.loader.api.metadata.CustomValue.CvType.STRING;

public class FabricPluginLoader extends PluginLoader {

    private static final Log LOG = Log.create();

    @Override
    protected void gatherPlugins() {
        Map<ModContainer, CustomValue.CvObject[]> pluginMap = new Object2ObjectOpenHashMap<>();
        for (var mod : FabricLoader.getInstance().getAllMods()) {
            for (var file : PLUGIN_JSON_FILES) {
                mod.findPath(file).ifPresent(path -> readPluginsJson(mod.getMetadata().getId(), path, Files::newBufferedReader));
            }

            var data = mod.getMetadata();

            if (!data.containsCustomValue("waila:plugins"))
                continue;

            var val = data.getCustomValue("waila:plugins");
            if (val.getType() == OBJECT) {
                pluginMap.put(mod, new CustomValue.CvObject[]{val.getAsObject()});
            } else if (val.getType() == ARRAY) {
                pluginMap.put(mod, Streams.stream(val.getAsArray()).map(CustomValue::getAsObject).toArray(CustomValue.CvObject[]::new));
            } else {
                LOG.error("Plugin data provided by {} must be an object or array of objects.", data.getId());
            }
        }

        for (var entry : pluginMap.entrySet()) {
            var mod = entry.getKey();
            var plugins = entry.getValue();

            o:
            for (var plugin : plugins) {
                List<String> requiredDeps = new ArrayList<>();
                if (plugin.containsKey("required")) {
                    var required = plugin.get("required");
                    if (required.getType() == STRING) {
                        if (FabricLoader.getInstance().isModLoaded(required.getAsString())) {
                            requiredDeps.add(required.getAsString());
                        } else {
                            continue;
                        }
                    }

                    if (required.getType() == ARRAY) {
                        for (var element : required.getAsArray()) {
                            if (element.getType() != STRING)
                                continue;

                            if (FabricLoader.getInstance().isModLoaded(element.getAsString())) {
                                requiredDeps.add(element.getAsString());
                            } else {
                                continue o;
                            }
                        }
                    }
                }

                var id = plugin.get("id").getAsString();
                var initializer = plugin.get("initializer").getAsString();

                var sideStr = plugin.containsKey("environment") ? plugin.get("environment").getAsString() : "both";
                PluginSide side;
                switch (sideStr) {
                    case "client" -> side = PluginSide.CLIENT;
                    case "server" -> side = PluginSide.DEDICATED_SERVER;
                    case "both" -> side = PluginSide.COMMON;
                    default -> {
                        LOG.error("Environment for plugin {} is not valid, must be one of [client, server, both].", id);
                        continue;
                    }
                }

                if (side == PluginSide.CLIENT && FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                    continue;
                }

                if (side == PluginSide.DEDICATED_SERVER && FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
                    continue;
                }

                PluginInfo.registerDeprecated(mod.getMetadata().getId(), id, side, initializer, requiredDeps, true, true);
            }
        }
    }

}
