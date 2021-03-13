package mcp.mobius.waila.fabric;

import com.google.common.collect.Streams;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaPlugins;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

public class WailaPluginsImpl extends WailaPlugins {

    public static void gatherPluginsInner() {
        FabricLoader.getInstance().getAllMods().stream()
            .map(ModContainer::getMetadata)
            .filter(modMetadata -> modMetadata.containsCustomValue("waila:plugins"))
            .map(m -> new PluginData(m.getId(), m.getCustomValue("waila:plugins")))
            .filter(d -> {
                if (d.value.getType() == CustomValue.CvType.OBJECT || d.value.getType() == CustomValue.CvType.ARRAY)
                    return true;

                Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject or a JsonArray.", d.id);
                return false;
            })
            .forEach(d -> {
                if (d.value.getType() == CustomValue.CvType.OBJECT) {
                    handlePluginData(d.value.getAsObject());
                } else {
                    Streams.stream(d.value.getAsArray())
                        .filter(e -> {
                            if (e.getType() == CustomValue.CvType.OBJECT)
                                return true;

                            Waila.LOGGER.error("Plugin data provided by {} must be a JsonObject.", d.id);
                            return false;
                        })
                        .map(CustomValue::getAsObject)
                        .forEach(WailaPluginsImpl::handlePluginData);
                }
            });
    }

    private static void handlePluginData(CustomValue.CvObject object) {
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

        String id = object.get("id").getAsString();
        String initializer = object.get("initializer").getAsString();
        createPlugin(id, initializer);
    }

    public static class PluginData {

        private final String id;
        private final CustomValue value;

        public PluginData(String id, CustomValue value) {
            this.id = id;
            this.value = value;
        }

    }

}
