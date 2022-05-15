package mcp.mobius.waila.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaPlugins;
import mcp.mobius.waila.overlay.TooltipRegistrar;
import mcp.mobius.waila.overlay.TooltipRegistry;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class DumpGenerator {

    public static final Map<String, String> VERSIONS = new LinkedHashMap<>();

    public static String generateInfoDump() {
        StringBuilder builder = new StringBuilder("# Waila "+ (Waila.clientSide ? "Client" : "Server")+" Dump");

        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;

        builder.append("\n## Versions");
        builder.append("\n| Dependency | Version |");
        builder.append("\n| - | - |");
        VERSIONS.forEach((k, v) -> builder.append("\n| ").append(k).append(" | `").append(v).append("` |"));

        builder.append("\n## Plugins");
        builder.append("\n| Plugin ID | Plugin Class |");
        builder.append("\n| - | - |");
        WailaPlugins.PLUGINS.keySet().stream()
            .sorted(String::compareToIgnoreCase)
            .forEachOrdered(id -> builder
                .append("\n| `")
                .append(id)
                .append("` | `")
                .append(WailaPlugins.PLUGINS.get(id).getClass().getCanonicalName())
                .append("` |"));

        builder.append("\n## Block");
        createSection(builder, "Override Providers", registrar.blockOverride);
        createSection(builder, "Display Item Providers", registrar.entityItem);
        createSection(builder, "Head Providers", registrar.blockComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.blockComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.blockComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.blockData);

        builder.append("\n## Entity");
        createSection(builder, "Override Providers", registrar.entityOverride);
        createSection(builder, "Display Item Providers", registrar.entityItem);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.entityData);

        return builder.toString();
    }

    private static <T> void createSection(StringBuilder builder, String subsection, TooltipRegistry<T> registry) {
        Map<Class<?>, Set<TooltipRegistry.Entry<T>>> map = registry.getMap();

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
                    .map(o -> o.value.getClass().getName())
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
