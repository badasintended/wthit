package mcp.mobius.waila.debug;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.registry.Register;
import mcp.mobius.waila.registry.Registrar;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class DumpGenerator {

    public static final Map<String, String> VERSIONS = new LinkedHashMap<>();

    public static boolean generate(Path path) {
        StringBuilder builder = new StringBuilder("# Waila Dump");

        Registrar registrar = Registrar.INSTANCE;

        builder.append("\n## Versions");
        builder.append("\n| Dependency | Version |");
        builder.append("\n| - | - |");
        VERSIONS.forEach((k, v) -> builder.append("\n| ").append(k).append(" | `").append(v).append("` |"));

        builder.append("\n## Plugins");
        builder.append("\n| Plugin ID | Plugin Class |");
        builder.append("\n| - | - |");
        PluginInfo.getAll().stream()
            .sorted(Comparator.comparing(IPluginInfo::getPluginId))
            .forEachOrdered(plugin -> builder
                .append("\n| `")
                .append(plugin.getPluginId())
                .append("` | `")
                .append(plugin.getInitializer().getClass().getCanonicalName())
                .append("` |"));

        builder.append("\n## Block");
        createSection(builder, "Override Providers", registrar.blockOverride);
        createSection(builder, "Display Item Providers", registrar.entityIcon);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.blockData);

        builder.append("\n## Entity");
        createSection(builder, "Override Providers", registrar.entityOverride);
        createSection(builder, "Display Item Providers", registrar.entityIcon);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.entityData);

        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(builder.toString());
            Waila.LOGGER.info("Created debug dump at {}", path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static <T> void createSection(StringBuilder builder, String subsection, Register<T> registry) {
        Map<Class<?>, List<Register.Entry<T>>> map = registry.getMap();

        if (map.isEmpty())
            return;

        builder.append("\n### ").append(subsection);
        builder.append("\n| Target Class | Provider Class |");
        builder.append("\n| - | - |");
        map.forEach((k, v) -> {
            if (!v.isEmpty()) {
                builder.append("\n| `").append(k.getName()).append("` ");
                int[] i = {0};
                v.stream()
                    .map(o -> o.value().getClass().getName())
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
