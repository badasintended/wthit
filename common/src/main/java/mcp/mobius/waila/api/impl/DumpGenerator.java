package mcp.mobius.waila.api.impl;

import java.util.Map;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class DumpGenerator {

    public static String generateInfoDump() {
        StringBuilder builder = new StringBuilder("# Waila Handler Dump");

        Registrar registrar = Registrar.INSTANCE;

        builder.append("\n## Block");
        createSection(builder, "Stack Providers", registrar.blockStack);
        createSection(builder, "Head Providers", registrar.blockComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.blockComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.blockComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.blockData);

        builder.append("\n## Entity");
        createSection(builder, "Override Providers", registrar.entityOverride);
        createSection(builder, "Head Providers", registrar.entityComponent.get(HEAD));
        createSection(builder, "Body Providers", registrar.entityComponent.get(BODY));
        createSection(builder, "Tail Providers", registrar.entityComponent.get(TAIL));
        createSection(builder, "Data Providers", registrar.entityData);

        return builder.toString();
    }

    private static <T, S extends Registrar.List<T>> void createSection(StringBuilder builder, String subsection, Map<Class<?>, S> providers) {
        if (providers.isEmpty())
            return;

        builder.append("\n### ").append(subsection);
        providers.forEach((k, v) -> {
            builder.append("\n\n#### ").append(k.getName());
            v.stream()
                .distinct()
                .map(o -> o.getClass().getName())
                .sorted(String::compareToIgnoreCase)
                .forEachOrdered(s -> builder.append("\n* ").append(s));
        });
        builder.append("\n\n");
    }

}
