package mcp.mobius.waila.api.__internal__;

import java.util.ServiceLoader;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

/** @hidden */
@ApiStatus.Internal
@SuppressWarnings("unchecked")
public final class Internals {

    private static final Unsafe UNSAFE;

    static {
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T unsafeAlloc(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadService(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

}
