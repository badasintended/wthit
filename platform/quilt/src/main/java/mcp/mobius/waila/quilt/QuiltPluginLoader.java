package mcp.mobius.waila.quilt;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import mcp.mobius.waila.util.Log;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.LoaderValue;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import static org.quiltmc.loader.api.LoaderValue.LType.ARRAY;
import static org.quiltmc.loader.api.LoaderValue.LType.OBJECT;
import static org.quiltmc.loader.api.LoaderValue.LType.STRING;

public class QuiltPluginLoader extends PluginLoader {

    private static final Log LOG = Log.create();

    @Override
    protected void gatherPlugins() {
        Map<ModContainer, LoaderValue.LObject[]> pluginMap = new Object2ObjectOpenHashMap<>();
        for (var mod : QuiltLoader.getAllMods()) {
            for (var file : PLUGIN_JSON_FILES) {
                var path = mod.getPath(file);
                if (Files.exists(path)) {
                    readPluginsJson(mod.metadata().id(), path);
                }
            }

            var data = mod.metadata();

            if (!data.containsValue("waila:plugins"))
                continue;

            var val = Objects.requireNonNull(data.value("waila:plugins"));
            if (val.type() == OBJECT) {
                pluginMap.put(mod, new LoaderValue.LObject[]{val.asObject()});
            } else if (val.type() == ARRAY) {
                pluginMap.put(mod, val.asArray().stream().map(LoaderValue::asObject).toArray(LoaderValue.LObject[]::new));
            } else {
                LOG.error("Plugin data provided by {} must be an object or array of objects.", data.id());
            }
        }

        for (var entry : pluginMap.entrySet()) {
            var mod = entry.getKey();
            var plugins = entry.getValue();

            o:
            for (var plugin : plugins) {
                List<String> requiredDeps = new ArrayList<>();
                if (plugin.containsKey("required")) {
                    var required = Objects.requireNonNull(plugin.get("required"));
                    if (required.type() == STRING) {
                        if (QuiltLoader.isModLoaded(required.asString())) {
                            requiredDeps.add(required.asString());
                        } else {
                            continue;
                        }
                    }

                    if (required.type() == ARRAY) {
                        for (var element : required.asArray()) {
                            if (element.type() != STRING)
                                continue;

                            if (QuiltLoader.isModLoaded(element.asString())) {
                                requiredDeps.add(element.asString());
                            } else {
                                continue o;
                            }
                        }
                    }
                }

                var id = Objects.requireNonNull(plugin.get("id")).asString();
                var initializer = Objects.requireNonNull(plugin.get("initializer")).asString();

                var sideStr = plugin.containsKey("environment") ? Objects.requireNonNull(plugin.get("environment")).asString() : "both";
                IPluginInfo.Side side;
                switch (sideStr) {
                    case "client" -> side = IPluginInfo.Side.CLIENT;
                    case "server" -> side = IPluginInfo.Side.SERVER;
                    case "both" -> side = IPluginInfo.Side.BOTH;
                    default -> {
                        LOG.error("Environment for plugin {} is not valid, must be one of [client, server, both].", id);
                        continue;
                    }
                }

                if (side == IPluginInfo.Side.CLIENT && MinecraftQuiltLoader.getEnvironmentType() != EnvType.CLIENT) {
                    continue;
                }

                if (side == IPluginInfo.Side.SERVER && MinecraftQuiltLoader.getEnvironmentType() != EnvType.SERVER) {
                    continue;
                }

                PluginInfo.register(mod.metadata().id(), id, side, initializer, requiredDeps, true, true);
            }
        }
    }

}
