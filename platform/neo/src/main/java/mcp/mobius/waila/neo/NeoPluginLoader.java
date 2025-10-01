package mcp.mobius.waila.neo;

import cpw.mods.jarhandling.JarResource;
import mcp.mobius.waila.plugin.PluginLoader;
import net.neoforged.fml.ModList;

public class NeoPluginLoader extends PluginLoader {

    @Override
    protected void gatherPlugins() {
        for (var modFile : ModList.get().getModFiles()) {
            for (var file : PLUGIN_JSON_FILES) {
                var modId = modFile.getMods().getFirst().getModId();
                var resource = modFile.getFile().getContents().get(file);
                if (resource != null) {
                    readPluginsJson(modId, resource, JarResource::bufferedReader);
                }
            }
        }
    }

}
