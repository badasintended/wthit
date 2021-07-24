package mcp.mobius.waila.fabric;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mcp.mobius.waila.PluginLoader;
import mcp.mobius.waila.Waila;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.CustomValue.CvObject;
import net.fabricmc.loader.api.metadata.ModMetadata;

import static net.fabricmc.loader.api.metadata.CustomValue.CvType.ARRAY;
import static net.fabricmc.loader.api.metadata.CustomValue.CvType.OBJECT;
import static net.fabricmc.loader.api.metadata.CustomValue.CvType.STRING;

public class FabricPluginLoader extends PluginLoader {

    @Override
    protected void gatherPlugins() {
        Set<CvObject> plugins = new ObjectOpenHashSet<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata data = mod.getMetadata();

            if (!data.containsCustomValue("waila:plugins"))
                continue;

            CustomValue val = data.getCustomValue("waila:plugins");
            if (val.getType() == OBJECT) {
                plugins.add(val.getAsObject());
            } else if (val.getType() == ARRAY) {
                val.getAsArray().forEach(o -> plugins.add(o.getAsObject()));
            } else {
                Waila.LOGGER.error("Plugin data provided by {} must be an object or array of objects.", data.getId());
            }
        }

        o:
        for (CvObject plugin : plugins) {
            if (plugin.containsKey("required")) {
                CustomValue required = plugin.get("required");
                if (required.getType() == STRING && !FabricLoader.getInstance().isModLoaded(required.getAsString()))
                    continue;

                if (required.getType() == ARRAY) {
                    for (CustomValue element : required.getAsArray()) {
                        if (element.getType() != STRING)
                            continue;

                        if (!FabricLoader.getInstance().isModLoaded(element.getAsString()))
                            continue o;
                    }
                }
            }

            String id = plugin.get("id").getAsString();
            String initializer = plugin.get("initializer").getAsString();

            createPlugin(id, initializer);
        }
    }

}
