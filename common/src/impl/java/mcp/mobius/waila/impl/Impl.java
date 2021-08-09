package mcp.mobius.waila.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@SuppressWarnings("unchecked")
public final class Impl {

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

}
