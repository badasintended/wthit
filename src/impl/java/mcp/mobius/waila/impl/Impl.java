package mcp.mobius.waila.impl;

import java.lang.reflect.Field;
import java.util.ServiceLoader;

import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

@ApiStatus.Internal
@SuppressWarnings("unchecked")
public final class Impl {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
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
