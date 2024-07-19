package mcp.mobius.waila.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private @Nullable T value;

    public CachedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return value == null ? value = supplier.get() : value;
    }

    public void invalidate() {
        this.value = null;
    }

}
