package mcp.mobius.waila.util;

import org.jspecify.annotations.Nullable;

@SuppressWarnings("unchecked")
public final class TypeUtil {

    @SuppressWarnings("DataFlowIssue")
    public static <T> T uncheckedCast(@Nullable Object object) {
        return (T) object;
    }

    public static <T> T tryCast(@Nullable Object object, Object defaultValue) {
        if (object == null) {
            return (T) defaultValue;
        }

        try {
            return (T) object;
        } catch (ClassCastException e) {
            return (T) defaultValue;
        }
    }

}
