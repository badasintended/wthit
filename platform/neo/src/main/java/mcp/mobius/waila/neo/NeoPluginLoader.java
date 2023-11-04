package mcp.mobius.waila.neo;

import java.nio.file.Files;

import mcp.mobius.waila.plugin.PluginLoader;
import net.neoforged.fml.ModList;

public class NeoPluginLoader extends PluginLoader {

    @Override
    protected void gatherPlugins() {
        for (var modFile : ModList.get().getModFiles()) {
            for (var file : PLUGIN_JSON_FILES) {
                var path = modFile.getFile().findResource(file);
                if (Files.exists(path)) {
                    readPluginsJson(modFile.getMods().get(0).getModId(), path);
                }
            }
        }
    }

}
