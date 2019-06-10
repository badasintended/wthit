package mcp.mobius.waila;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.List;
import java.util.Map;

public class WailaPlugins {

    public static final Map<String, IWailaPlugin> PLUGINS = Maps.newHashMap();

    public static void gatherPlugins() {
        PLUGINS.clear();

        FabricLoader.getInstance().getAllMods().stream()
                .map(ModContainer::getMetadata)
                .filter(modMetadata -> modMetadata.containsCustomElement("waila:plugins"))
                .map(m -> new PluginData(m.getId(), m.getCustomElement("waila:plugins"), m))
                .filter(d -> {
                    if (d.json.isJsonObject() || d.json.isJsonArray())
                        return true;

                    Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject or a JsonArray.", d.id);
                    return false;
                })
                .forEach(d -> {
                    if (d.json.isJsonObject()) {
                        handlePluginData(d, d.json.getAsJsonObject());
                    } else {
                        Streams.stream(d.json.getAsJsonArray())
                                .filter(e -> {
                                    if (e.isJsonObject())
                                        return true;

                                    Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject.", d.id);
                                    return false;
                                })
                                .map(JsonElement::getAsJsonObject)
                                .forEach(jsonObject -> handlePluginData(d, jsonObject));
                    }
                });
    }

    public static void initializePlugins() {
        Waila.LOGGER.info("Registering plugin at {}", PluginCore.class.getCanonicalName());
        PLUGINS.remove("waila:core").register(WailaRegistrar.INSTANCE); // Handle and clear the core plugin so it's registered first

        List<IWailaPlugin> sorted = Lists.newArrayList(PLUGINS.values());
        sorted.sort((o1, o2) -> {
            // Don't move waila classes when compared to eachother
            if (isWailaClass(o1) && isWailaClass(o2))
                return 0;

            // Move waila plugins to the top
            if (isWailaClass(o1))
                return -1;

            return o1.getClass().getCanonicalName().compareToIgnoreCase(o2.getClass().getCanonicalName());
        });

        sorted.forEach(p -> {
            Waila.LOGGER.info("Registering plugin at {}", p.getClass().getCanonicalName());
            p.register(WailaRegistrar.INSTANCE);
        });
        PluginConfig.INSTANCE.reload();
    }

    private static void handlePluginData(PluginData data, JsonObject json) {
        if (json.has("required")) {
            JsonElement required = json.get("required");
            if (required.isJsonPrimitive() && !FabricLoader.getInstance().isModLoaded(required.getAsJsonPrimitive().getAsString()))
                return;

            if (required.isJsonArray()) {
                for (JsonElement element : required.getAsJsonArray()) {
                    if (!element.isJsonPrimitive())
                        continue;

                    if (!FabricLoader.getInstance().isModLoaded(element.getAsString()))
                        return;
                }
            }
        }

        // TODO Revisit if fabric ever makes language adapters accessible again
        String id = json.getAsJsonPrimitive("id").getAsString();
        String initializer = json.getAsJsonPrimitive("initializer").getAsString();
        try {
            IWailaPlugin plugin = (IWailaPlugin) Class.forName(initializer).newInstance();
            PLUGINS.put(id, plugin);
            Waila.LOGGER.info("Discovered plugin {} provided by {} at {}", id, data.metadata.getId(), plugin.getClass().getCanonicalName());
        } catch (Exception e) {
            Waila.LOGGER.error("Error creating instance of plugin {} provided by {}", id, data.metadata.getId());
        }
    }

    private static boolean isWailaClass(Object object) {
        return object.getClass().getCanonicalName().startsWith("mcp.mobius.waila");
    }

    public static class PluginData {
        private final String id;
        private final JsonElement json;
        private final ModMetadata metadata;

        public PluginData(String id, JsonElement json, ModMetadata metadata) {
            this.id = id;
            this.json = json;
            this.metadata = metadata;
        }
    }
}
