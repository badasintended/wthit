package mcp.mobius.waila.forge;

import java.util.Arrays;

import mcp.mobius.waila.WailaPlugins;
import mcp.mobius.waila.api.WailaForgePlugin;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.ModList;

public class ForgeWailaPlugins extends WailaPlugins {

    static final String[] EMPTY = new String[0];
    static final String WAILA_PLUGIN = WailaPlugin.class.getName();
    static final String WAILA_FORGE_PLUGIN = WailaForgePlugin.class.getName();

    public void gatherPluginsInner() {
        ModList.get().getAllScanData().forEach(data -> data.getAnnotations().forEach(annotation -> {
            if (annotation.getAnnotationType().getClassName().equals(WAILA_FORGE_PLUGIN)) {
                String id = (String) annotation.getAnnotationData().get("value");
                String[] dep = (String[]) annotation.getAnnotationData().getOrDefault("requires", EMPTY);
                if (dep.length == 0 || Arrays.stream(dep).allMatch(ModList.get()::isLoaded)) {
                    createPlugin(id, annotation.getMemberName());
                }
            } else if (annotation.getAnnotationType().getClassName().equals(WAILA_PLUGIN)) {
                String required = (String) annotation.getAnnotationData().getOrDefault("value", "");
                if (required.isEmpty() || ModList.get().isLoaded(required)) {
                    createPlugin(annotation.getMemberName(), annotation.getMemberName());
                }
            }
        }));
    }

}
