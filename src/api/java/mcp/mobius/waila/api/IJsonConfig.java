package mcp.mobius.waila.api;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.List;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    /**
     * Creates a builder for concrete config type.
     */
    static <T> Builder0<T> of(Class<T> clazz) {
        return IApiService.INSTANCE.createConfigBuilder(clazz);
    }

    /**
     * Creates a builder for generic config type.
     * <p>
     * <b>Note</b>: the {@linkplain Builder1#factory(Supplier) object factory} must be specified.
     */
    static <T> Builder0<T> of(TypeToken<T> type) {
        return IApiService.INSTANCE.createConfigBuilder(type.getType());
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
     * @return the path to the config file.
     */
    Path getPath();

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

    /**
     * Adds comment for this value.
     *
     * @see Builder1#commenter(Supplier)
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Comment {

        /**
         * The comment.
         */
        String value();

    }

    /**
     * A custom commenter.
     */
    interface Commenter {

        /**
         * Returns the comment for the specified path.
         *
         * @param path a list containing the nested path of the entry, empty list means the root object
         */
        @Nullable
        String getComment(List<String> path);

    }

    interface Builder0<T> {

        Builder1<T> file(File file);

        Builder1<T> file(Path path);

        Builder1<T> file(String fileName);

    }

    interface Builder1<T> {

        Builder1<T> version(int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter);

        Builder1<T> json5();

        Builder1<T> commenter(Supplier<Commenter> commenter);

        Builder1<T> gson(Gson gson);

        Builder1<T> factory(Supplier<T> factory);

        IJsonConfig<T> build();

    }

}
