package mcp.mobius.waila.paper;

import java.io.IOException;
import java.io.InputStreamReader;

import mcp.mobius.waila.plugin.PluginLoader;

public class PaperPluginLoader extends PluginLoader {

    @Override
    protected void gatherPlugins() {
        for (var file : PLUGIN_JSON_FILES) {
            var resource = getClass().getClassLoader().getResourceAsStream(file);
            if (resource != null) {
                try (resource) {
                    readPluginsJson("wthit", resource, InputStreamReader::new);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to close plugin resource: " + file, e);
                }
            }
        }
    }

}
