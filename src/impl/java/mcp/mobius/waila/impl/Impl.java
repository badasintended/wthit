package mcp.mobius.waila.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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

    private static final Map<Class<?>, Object> MAP = new HashMap<>();

    public static <T> T get(Class<T> clazz) {
        return ((Supplier<T>) MAP.get(clazz)).get();
    }

    public static <T, A> T get(Class<T> clazz, A arg) {
        return ((Function<A, T>) MAP.get(clazz)).apply(arg);
    }

    public static <T> void reg(Class<T> clazz, Supplier<T> supplier) {
        MAP.put(clazz, supplier);
    }

    public static <T, A> void reg(Class<T> clazz, Function<A, T> func) {
        MAP.put(clazz, func);
    }

    public static <T> T allocate(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}
