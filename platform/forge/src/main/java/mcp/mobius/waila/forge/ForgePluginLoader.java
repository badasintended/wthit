package mcp.mobius.waila.forge;

import java.nio.file.Files;
import java.util.Arrays;

import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePluginLoader extends PluginLoader {

    @SuppressWarnings("deprecation")
    private static final String WAILA_PLUGIN = WailaPlugin.class.getName();

    @Override
    protected void gatherPlugins() {
        for (var modFile : ModList.get().getModFiles()) {
            for (var file : PLUGIN_JSON_FILES) {
                var path = modFile.getFile().findResource(file);
                if (Files.exists(path)) {
                    readPluginsJson(modFile.getMods().get(0).getModId(), path);
                }
            }

            for (var annotation : modFile.getFile().getScanResult().getAnnotations()) {
                if (annotation.annotationType().getClassName().equals(WAILA_PLUGIN)) {
                    var id = (String) annotation.annotationData().get("id");
                    var required = (String[]) annotation.annotationData().getOrDefault("required", new String[0]);
                    var side = (IPluginInfo.Side) annotation.annotationData().get("side");

                    var satisfied = true;
                    for (var dep : required) {
                        satisfied &= ModList.get().isLoaded(dep);
                    }

                    if (side == IPluginInfo.Side.CLIENT && FMLLoader.getDist() != Dist.CLIENT) {
                        satisfied = false;
                    }

                    if (side == IPluginInfo.Side.SERVER && FMLLoader.getDist() != Dist.DEDICATED_SERVER) {
                        satisfied = false;
                    }

                    if (satisfied) {
                        PluginInfo.register(modFile.getMods().get(0).getModId(), id, side, annotation.memberName(), Arrays.asList(required), true);
                    }
                }
            }
        }
    }

}
