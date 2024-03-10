package mcp.mobius.waila.util;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;

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
