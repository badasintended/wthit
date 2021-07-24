package mcp.mobius.waila.forge;

import mcp.mobius.waila.PluginLoader;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.ModList;

public class ForgePluginLoader extends PluginLoader {

    static final String WAILA_PLUGIN = WailaPlugin.class.getName();

    @Override
    public void gatherPlugins() {
        ModList.get().getAllScanData().forEach(data -> data.getAnnotations().forEach(annotation -> {
            if (annotation.annotationType().getClassName().equals(WAILA_PLUGIN)) {
                String id = (String) annotation.annotationData().get("id");
                String[] required = (String[]) annotation.annotationData().getOrDefault("required", new String[0]);

                boolean satisfied = true;
                for (String dep : required) {
                    satisfied &= ModList.get().isLoaded(dep);
                }

                if (satisfied) {
                    createPlugin(id, annotation.memberName());
                }
            }
        }));
    }

}
