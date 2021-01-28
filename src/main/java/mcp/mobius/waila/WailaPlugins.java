package mcp.mobius.waila;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class WailaPlugins {

    public static final Map<String, IWailaPlugin> PLUGINS = Maps.newHashMap();

    public static void gatherPlugins() {
        PLUGINS.clear();

        FabricLoader.getInstance().getAllMods().stream()
            .map(ModContainer::getMetadata)
            .filter(modMetadata -> modMetadata.containsCustomValue("waila:plugins"))
            .map(m -> new PluginData(m.getId(), m.getCustomValue("waila:plugins"), m))
            .filter(d -> {
                if (d.value.getType() == CustomValue.CvType.OBJECT || d.value.getType() == CustomValue.CvType.ARRAY)
                    return true;

                Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject or a JsonArray.", d.id);
                return false;
            })
            .forEach(d -> {
                if (d.value.getType() == CustomValue.CvType.OBJECT) {
                    handlePluginData(d, d.value.getAsObject());
                } else {
                    Streams.stream(d.value.getAsArray())
                        .filter(e -> {
                            if (e.getType() == CustomValue.CvType.OBJECT)
                                return true;

                            Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject.", d.id);
                            return false;
                        })
                        .map(CustomValue::getAsObject)
                        .forEach(cvObject -> handlePluginData(d, cvObject));
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

    private static void handlePluginData(PluginData data, CustomValue.CvObject object) {
        if (object.containsKey("required")) {
            CustomValue required = object.get("required");
            if (required.getType() == CustomValue.CvType.STRING && !FabricLoader.getInstance().isModLoaded(required.getAsString()))
                return;

            if (required.getType() == CustomValue.CvType.ARRAY) {
                for (CustomValue element : required.getAsArray()) {
                    if (element.getType() != CustomValue.CvType.STRING)
                        continue;

                    if (!FabricLoader.getInstance().isModLoaded(element.getAsString()))
                        return;
                }
            }
        }

        // TODO Revisit if fabric ever makes language adapters accessible again
        String id = object.get("id").getAsString();
        String initializer = object.get("initializer").getAsString();
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
        private final CustomValue value;
        private final ModMetadata metadata;

        public PluginData(String id, CustomValue value, ModMetadata metadata) {
            this.id = id;
            this.value = value;
            this.metadata = metadata;
        }

    }

}
