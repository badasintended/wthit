package mcp.mobius.waila.util;

@SuppressWarnings("unchecked")
public final class TypeUtil {

    public static <T> T uncheckedCast(Object object) {
        return (T) object;
    }

    public static <T> T tryCast(Object object, Object defaultValue) {
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
