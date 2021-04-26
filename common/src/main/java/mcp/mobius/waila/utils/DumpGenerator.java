package mcp.mobius.waila.utils;

import java.util.List;
import java.util.Map;

import mcp.mobius.waila.overlay.TooltipRegistrar;
import mcp.mobius.waila.overlay.TooltipRegistry;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class DumpGenerator {

    public static String generateInfoDump() {
        StringBuilder builder = new StringBuilder("# Waila Handler Dump");

        TooltipRegistrar registrar = TooltipRegistrar.INSTANCE;

        builder.append("\n## Block");
        createSection(builder, "Override Providers", registrar.blockOverride);
        createSection(builder, "Display Item Providers", registrar.entityItem);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
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
        Map<Class<?>, List<TooltipRegistry.Entry<T>>> map = registry.getMap();

        if (map.isEmpty())
            return;

        builder.append("\n### ").append(subsection);
        map.forEach((k, v) -> {
            if (!v.isEmpty()) {
                builder.append("\n\n#### `").append(k.getName()).append('`');
                v.stream()
                    .map(o -> o.value.getClass().getName())
                    .distinct()
                    .sorted(String::compareToIgnoreCase)
                    .forEachOrdered(s -> builder.append("\n* `").append(s).append('`'));
            }
        });
        builder.append("\n\n");
    }

}
