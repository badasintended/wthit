package mcp.mobius.waila.forge;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaPlugins;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.ModList;

public class ForgeWailaPlugins extends WailaPlugins {

    // TODO: Remove in 1.17 release
    static final String WAILA_PLUGIN = WailaPlugin.class.getName();

    @Override
    public void gatherPlugins() {
        ModList.get().getModFiles().forEach(modFile ->
            modFile.getConfigList("wailaPlugins").forEach(map -> {
                String id = map.<String>getConfigElement("id").get();
                String initializer = map.<String>getConfigElement("initializer").get();

                final boolean[] satisfied = {true};
                map.getConfigElement("required").ifPresent(required -> {
                    if (required instanceof String) {
                        satisfied[0] = ModList.get().isLoaded((String) required);
                    } else if (required instanceof List) {
                        for (String s : ((List<String>) required)) {
                            satisfied[0] &= ModList.get().isLoaded(s);
                        }
                    }
                });

                if (satisfied[0]) {
                    createPlugin(id, initializer);
                }
            }));

        // TODO: Remove in 1.17 release
        ModList.get().getAllScanData().forEach(data -> data.getAnnotations().forEach(annotation -> {
            if (annotation.getAnnotationType().getClassName().equals(WAILA_PLUGIN)) {
                String required = (String) annotation.getAnnotationData().getOrDefault("value", "");
                if (required.isEmpty() || ModList.get().isLoaded(required)) {
                    Waila.LOGGER.warn("Waila plugin {} is defined by deprecated method", annotation.getMemberName());
                    createPlugin("legacy:" + annotation.getMemberName(), annotation.getMemberName());
                }
            }
        }));
    }

}
