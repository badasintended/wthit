package mcp.mobius.waila.quilt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.LoaderValue;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.ModMetadata;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import static org.quiltmc.loader.api.LoaderValue.LType.ARRAY;
import static org.quiltmc.loader.api.LoaderValue.LType.OBJECT;
import static org.quiltmc.loader.api.LoaderValue.LType.STRING;

public class QuiltPluginLoader extends PluginLoader {

    @Override
    protected void gatherPlugins() {
        Map<ModContainer, LoaderValue.LObject[]> pluginMap = new Object2ObjectOpenHashMap<>();
        for (ModContainer mod : QuiltLoader.getAllMods()) {
            Path pluginJson = mod.getPath(PLUGIN_JSON_PATH);
            if (Files.exists(pluginJson)) {
                readPluginsJson(mod.metadata().id(), pluginJson);
            }

            ModMetadata data = mod.metadata();

            if (!data.containsValue("waila:plugins"))
                continue;

            LoaderValue val = Objects.requireNonNull(data.value("waila:plugins"));
            if (val.type() == OBJECT) {
                pluginMap.put(mod, new LoaderValue.LObject[]{val.asObject()});
            } else if (val.type() == ARRAY) {
                pluginMap.put(mod, val.asArray().stream().map(LoaderValue::asObject).toArray(LoaderValue.LObject[]::new));
            } else {
                Waila.LOGGER.error("Plugin data provided by {} must be an object or array of objects.", data.id());
            }
        }

        for (Map.Entry<ModContainer, LoaderValue.LObject[]> entry : pluginMap.entrySet()) {
            ModContainer mod = entry.getKey();
            LoaderValue.LObject[] plugins = entry.getValue();

            o:
            for (LoaderValue.LObject plugin : plugins) {
                List<String> requiredDeps = new ArrayList<>();
                if (plugin.containsKey("required")) {
                    LoaderValue required = Objects.requireNonNull(plugin.get("required"));
                    if (required.type() == STRING) {
                        if (QuiltLoader.isModLoaded(required.asString())) {
                            requiredDeps.add(required.asString());
                        } else {
                            continue;
                        }
                    }

                    if (required.type() == ARRAY) {
                        for (LoaderValue element : required.asArray()) {
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

                String id = Objects.requireNonNull(plugin.get("id")).asString();
                String initializer = Objects.requireNonNull(plugin.get("initializer")).asString();

                String sideStr = plugin.containsKey("environment") ? Objects.requireNonNull(plugin.get("environment")).asString() : "both";
                IPluginInfo.Side side;
                switch (sideStr) {
                    case "client" -> side = IPluginInfo.Side.CLIENT;
                    case "server" -> side = IPluginInfo.Side.SERVER;
                    case "both" -> side = IPluginInfo.Side.BOTH;
                    default -> {
                        Waila.LOGGER.error("Environment for plugin {} is not valid, must be one of [client, server, both].", id);
                        continue;
                    }
                }

                if (side == IPluginInfo.Side.CLIENT && MinecraftQuiltLoader.getEnvironmentType() != EnvType.CLIENT) {
                    continue;
                }

                if (side == IPluginInfo.Side.SERVER && MinecraftQuiltLoader.getEnvironmentType() != EnvType.SERVER) {
                    continue;
                }

                PluginInfo.register(mod.metadata().id(), id, side, initializer, requiredDeps, true);
            }
        }
    }

}
