package mcp.mobius.waila.neo;

import java.nio.file.Files;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.plugin.DefaultPluginDiscoverer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class NeoPluginDiscoverer extends DefaultPluginDiscoverer {

    public static final ResourceLocation ID = Waila.id("neo");

    @Override
    public ResourceLocation getDiscovererId() {
        return ID;
    }

    @Override
    public void discover(Candidates candidates) {
        for (var modFile : ModList.get().getModFiles()) {
            for (var file : PLUGIN_JSON_FILES) {
                var path = modFile.getFile().findResource(file);
                if (Files.exists(path)) {
                    readPluginsJson(candidates, modFile.getMods().get(0).getModId(), path);
                }
            }
        }
    }

}
