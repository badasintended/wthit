package mcp.mobius.waila.config;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.mcless.config.ConfigIo;
import mcp.mobius.waila.util.CachedSupplier;
import mcp.mobius.waila.util.Log;
import org.jetbrains.annotations.Nullable;

public class JsonConfig<T> implements IJsonConfig<T> {

    public static final Set<JsonConfig<Object>> INSTANCES = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));

    private static final Log LOG = Log.create();

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    private final Path path;
    private final ConfigIo<T> io;
    private final CachedSupplier<T> getter;

    @SuppressWarnings("unchecked")
    JsonConfig(Path path, Type type, Supplier<T> factory, boolean json5, Supplier<Commenter> commenter, Gson gson, int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter) {
        this.path = path.toAbsolutePath();

        var commenterFactories = new ArrayList<Supplier<Commenter>>();
        if (type instanceof Class<?> cls) commenterFactories.add(() -> new AnnotationCommenter(cls, gson));
        commenterFactories.add(commenter);

        this.io = new ConfigIo<>(LOG::warn, LOG::error, json5, new CommenterFactories(commenterFactories), gson, type, factory, currentVersion, versionGetter, versionSetter);
        this.getter = new CachedSupplier<>(() -> io.read(this.path));

        INSTANCES.add((JsonConfig<Object>) this);
    }

    public static void reloadAllInstances() {
        INSTANCES.forEach(it -> it.write(it.get(), true));
    }

    private void write(T t, Path path, boolean invalidate) {
        if (io.write(path, t) && invalidate) {
            invalidate();
        }
    }

    @Override
    public boolean isFileExists() {
        return Files.exists(path);
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public T get() {
        return getter.get();
    }

    @Override
    public void save() {
        write(get(), false); // Does not need to invalidate since the saved instance already has updated values
    }

    @Override
    public void write(T t, boolean invalidate) {
        write(t, path, invalidate);
    }

    @Override
    public void invalidate() {
        getter.invalidate();
    }

    @Override
    public void backup(@Nullable String cause) {
        if (isFileExists()) {
            var backupPath = Paths.get(path + "_" + DATE_FORMAT.format(new Date()));
            var msg = "Config " + path.getFileName() + " is getting backup to " + backupPath;
            if (cause != null) {
                msg += " because of " + cause;
            }
            LOG.info(msg);
            write(get(), backupPath, true);
        }
    }

    public static class Builder<T> implements Builder0<T>, Builder1<T> {

        final Type type;
        Supplier<Path> path;
        boolean json5;
        Supplier<Commenter> commenter;
        Gson gson;
        int currentVersion;
        ToIntFunction<T> versionGetter;
        ObjIntConsumer<T> versionSetter;
        Supplier<T> factory;

        @SuppressWarnings("unchecked")
        public Builder(Type type) {
            this.type = type;
            this.json5 = false;
            this.commenter = () -> s -> null;
            this.gson = DEFAULT_GSON;
            this.currentVersion = 0;
            this.versionGetter = t -> 0;
            this.versionSetter = (t, v) -> {};

            if (type instanceof Class<?> clazz) this.factory = () -> {
                try {
                    return (T) clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create new config instance", e);
                }
            };
        }

        @Override
        public Builder1<T> file(File file) {
            this.path = file::toPath;
            return this;
        }

        @Override
        public Builder1<T> file(Path path) {
            this.path = () -> path;
            return this;
        }

        @Override
        public Builder1<T> file(String fileName) {
            this.path = () -> {
                var path = fileName;
                if (json5) {
                    if (!path.endsWith(".json5")) path += ".json5";
                } else {
                    if (!path.endsWith(".json")) path += ".json";
                }

                return Waila.CONFIG_DIR.resolve(path);
            };

            return this;
        }

        @Override
        public Builder1<T> version(int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter) {
            this.currentVersion = currentVersion;
            this.versionGetter = versionGetter;
            this.versionSetter = versionSetter;
            return this;
        }

        @Override
        public Builder1<T> factory(Supplier<T> factory) {
            this.factory = factory;
            return this;
        }

        @Override
        public Builder1<T> json5() {
            this.json5 = true;
            return this;
        }

        @Override
        public Builder1<T> commenter(Supplier<Commenter> commenter) {
            this.commenter = commenter;
            return this;
        }

        @Override
        public Builder1<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        @Override
        public IJsonConfig<T> build() {
            Preconditions.checkNotNull(factory, "Default value factory must not be null");
            return new JsonConfig<>(path.get(), type, factory, json5, commenter, gson, currentVersion, versionGetter, versionSetter);
        }

    }

}
