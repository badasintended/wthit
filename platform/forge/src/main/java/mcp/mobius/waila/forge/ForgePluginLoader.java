package mcp.mobius.waila.forge;

import java.util.Arrays;

import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.plugin.PluginLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;

public class ForgePluginLoader extends PluginLoader {

    private static final String WAILA_PLUGIN = WailaPlugin.class.getName();

    @Override
    protected void gatherPlugins() {
        for (IModFileInfo modFile : ModList.get().getModFiles()) {
            for (ModFileScanData.AnnotationData annotation : modFile.getFile().getScanResult().getAnnotations()) {
                if (annotation.annotationType().getClassName().equals(WAILA_PLUGIN)) {
                    String id = (String) annotation.annotationData().get("id");
                    String[] required = (String[]) annotation.annotationData().getOrDefault("required", new String[0]);
                    IPluginInfo.Side side = (IPluginInfo.Side) annotation.annotationData().get("side");

                    boolean satisfied = true;
                    for (String dep : required) {
                        satisfied &= ModList.get().isLoaded(dep);
                    }

                    if (side == IPluginInfo.Side.CLIENT && FMLLoader.getDist() != Dist.CLIENT) {
                        satisfied = false;
                    }

                    if (side == IPluginInfo.Side.SERVER && FMLLoader.getDist() != Dist.DEDICATED_SERVER) {
                        satisfied = false;
                    }

                    if (satisfied) {
                        PluginInfo.register(modFile.getMods().get(0).getModId(), id, side, annotation.memberName(), Arrays.asList(required));
                    }
                }
            }
        }
    }

}
