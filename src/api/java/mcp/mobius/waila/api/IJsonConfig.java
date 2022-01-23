package mcp.mobius.waila.api;

import java.io.File;
import java.nio.file.Path;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.Gson;
import mcp.mobius.waila.api.__internal__.IApiService;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * An Interface for easy (de)serialization for config classes
 *
 * @param <T> the config class
 */
@ApiStatus.NonExtendable
public interface IJsonConfig<T> {

    static <T> Builder0<T> of(Class<T> clazz) {
        return IApiService.INSTANCE.createConfigBuilder(clazz);
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

    /**
     * @return whether the config file exists.
     */
    boolean isFileExists();

    /**
     * Backup and invalidate current value and force re-read file on next {@link #get}.
     * The backup will have a time suffix to it's file extension.
     */
    void backup(@Nullable String cause);

    /**
     * @see #backup(String)
     */
    default void backup() {
        this.backup(null);
    }

    interface Builder0<T> {

        Builder1<T> file(File file);

        Builder1<T> file(Path path);

        Builder1<T> file(String fileName);

    }

    interface Builder1<T> {

        Builder1<T> version(int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter);

        Builder1<T> gson(Gson gson);

        Builder1<T> factory(Supplier<T> factory);

        IJsonConfig<T> build();

    }

}
