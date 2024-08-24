package mcp.mobius.waila.debug;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.registry.InstanceRegistry;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.Log;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.gui.hud.TooltipPosition.BODY;
import static mcp.mobius.waila.gui.hud.TooltipPosition.HEAD;
import static mcp.mobius.waila.gui.hud.TooltipPosition.TAIL;

public class DumpGenerator {

    private static final Log LOG = Log.create();

    public static final String LOCAL = "local_dump";
    public static final String SERVER = "server_dump";
    public static final String CLIENT = "client_dump";
    public static final Map<String, String> VERSIONS = new LinkedHashMap<>();

    @Nullable
    public static Path generate(String name) {
        var path = Waila.GAME_DIR.resolve(".waila/" + name + ".md").toAbsolutePath();

        //noinspection ResultOfMethodCallIgnored
        path.getParent().toFile().mkdirs();

        var builder = new StringBuilder("# Waila Dump");

        var registrar = Registrar.get();

        builder.append("\n## Versions");
        builder.append("\n| Dependency | Version |");
        builder.append("\n| - | - |");
        VERSIONS.forEach((k, v) -> builder.append("\n| ").append(k).append(" | `").append(v).append("` |"));

        builder.append("\n## Plugins");
        builder.append("\n| Plugin ID | Plugin Class | Mod Name | Mod Version |");
        builder.append("\n| - | - | - | - |");
        PluginInfo.getAll().stream()
            .sorted(Comparator.comparing(IPluginInfo::getPluginId))
            .forEachOrdered(plugin -> builder
                .append("\n| `")
                .append(plugin.getPluginId())
                .append("` | `")
                .append(plugin.getInitializer().getClass().getCanonicalName())
                .append("` | ")
                .append(plugin.getModInfo().getName())
                .append(" | `")
                .append(plugin.getModInfo().getVersion())
                .append("` |"));

        builder.append("\n## Block");
        createSection(builder, "Override Providers", registrar.blockOverride);
        createSection(builder, "Display Icon Providers", registrar.blockIcon);
        createSection(builder, "Head Providers", registrar.blockComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.blockComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.blockComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.blockData);

        builder.append("\n## Entity");
        createSection(builder, "Override Providers", registrar.entityOverride);
        createSection(builder, "Display Icon Providers", registrar.entityIcon);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.entityData);

        try (var writer = new FileWriter(path.toFile())) {
            writer.write(builder.toString());
            LOG.info("Created debug dump at {}", path);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> void createSection(StringBuilder builder, String subsection, InstanceRegistry<T> registry) {
        var map = registry.getMap();

        if (map.isEmpty())
            return;

        builder.append("\n### ").append(subsection);
        builder.append("\n| Target Class | Provider Class |");
        builder.append("\n| - | - |");
        map.forEach((k, v) -> {
            if (!v.isEmpty()) {
                builder.append("\n| `").append(k.getName()).append("` ");
                var i = new int[]{0};
                v.stream()
                    .map(o -> o.instance().getClass().getName())
                    .distinct()
                    .sorted(String::compareToIgnoreCase)
                    .forEachOrdered(s -> {
                        if (i[0] == 0) {
                            builder.append("| `").append(s).append("` |");
                        } else {
                            builder.append("\n| | `").append(s).append("` |");
                        }
                        i[0]++;
                    });
            }
        });
        builder.append("\n\n");
    }

}
