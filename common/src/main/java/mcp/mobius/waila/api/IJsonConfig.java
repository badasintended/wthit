package mcp.mobius.waila.api;

import java.io.File;
import java.util.function.Supplier;

import com.google.gson.Gson;
import mcp.mobius.waila.utils.JsonConfig;

/**
 * An Interface for easy (de)serialization for config classes
 * @param <T> the config class
 */
public interface IJsonConfig<T> {

    static <T> Builder0<T> of(Class<T> clazz) {
        return new JsonConfig.Builder<>(clazz);
    }

    /**
     * Get current value
     */
    T get();

    /**
     * Save value to file
     */
    void save();

    /**
     * Write value to file
     */
    void write(T t, boolean invalidate);

    /**
     * Invalidate current value and force re-read file on next {@link #get}
     */
    void invalidate();

    interface Builder0<T> {

        Builder1<T> file(File file);

        Builder1<T> file(String fileName);

    }

    interface Builder1<T> {

        Builder1<T> gson(Gson gson);

        Builder1<T> factory(Supplier<T> factory);

        IJsonConfig<T> build();

    }

}
