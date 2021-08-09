package mcp.mobius.waila.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.util.CommonUtil;

public class JsonConfig<T> implements IJsonConfig<T> {

    @SuppressWarnings("rawtypes")
    private static final ToIntFunction DEFAULT_VERSION_GETTER = t -> 0;

    @SuppressWarnings("rawtypes")
    private static final ObjIntConsumer DEFAULT_VERSION_SETTER = (t, v) -> {};

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;
    private final CachedSupplier<T> getter;
    private final Gson gson;

    JsonConfig(Path path, Class<T> clazz, Supplier<T> factory, Gson gson, int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter) {
        this.path = path.toAbsolutePath();
        this.gson = gson;
        this.getter = new CachedSupplier<>(() -> {
            T config;
            boolean init = true;
            if (!Files.exists(this.path)) {
                Path parent = this.path.getParent();
                if (!Files.exists(parent)) {
                    try {
                        Files.createDirectories(parent);
                    } catch (IOException e) {
                        CommonUtil.LOGGER.error("Failed to make directory " + parent, e);
                    }
                }
                config = factory.get();
            } else {
                try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
                    config = this.gson.fromJson(reader, clazz);
                    int version = versionGetter.applyAsInt(config);
                    if (version != currentVersion) {
                        Path old = Paths.get(this.path + "_old");
                        CommonUtil.LOGGER.warn("Config file "
                            + this.path
                            + " contains different version ("
                            + version
                            + ") than required version ("
                            + currentVersion
                            + "), this config will be reset. Old config will be placed at "
                            + old);
                        Files.deleteIfExists(old);
                        Files.copy(this.path, old);
                        config = factory.get();
                    } else {
                        init = false;
                    }
                } catch (Exception e) {
                    Path old = Paths.get(this.path + "_old");
                    CommonUtil.LOGGER.error("Exception when reading config file "
                        + this.path
                        + ", this config will be reset. Old config will be placed at "
                        + old, e);
                    try {
                        Files.deleteIfExists(old);
                        Files.copy(this.path, old);
                    } catch (IOException e1) {
                        CommonUtil.LOGGER.error("well this is embarrassing...", e1);
                    }
                    config = factory.get();
                }
            }
            if (init) {
                versionSetter.accept(config, currentVersion);
                write(config, false);
            }
            return config;
        });
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
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(t));
            if (invalidate)
                invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invalidate() {
        getter.invalidate();
    }

    public static class Builder<T> implements Builder0<T>, Builder1<T> {

        final Class<T> clazz;
        Path path;
        Gson gson;
        int currentVersion;
        ToIntFunction<T> versionGetter;
        ObjIntConsumer<T> versionSetter;
        Supplier<T> factory;

        @SuppressWarnings("unchecked")
        public Builder(Class<T> clazz) {
            this.clazz = clazz;
            this.gson = DEFAULT_GSON;
            this.currentVersion = 0;
            this.versionGetter = DEFAULT_VERSION_GETTER;
            this.versionSetter = DEFAULT_VERSION_SETTER;
            this.factory = () -> {
                try {
                    return clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create new config instance", e);
                }
            };
        }

        public static <T> Builder<T> create(Class<T> clazz) {
            return new Builder<>(clazz);
        }

        @Override
        public Builder1<T> file(File file) {
            this.path = file.toPath();
            return this;
        }

        @Override
        public Builder1<T> file(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public Builder1<T> file(String fileName) {
            this.path = CommonUtil.configDir.resolve(fileName + (fileName.endsWith(".json") ? "" : ".json"));
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
        public Builder1<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        @Override
        public IJsonConfig<T> build() {
            return new JsonConfig<>(path, clazz, factory, gson, currentVersion, versionGetter, versionSetter);
        }

    }

    static class CachedSupplier<T> {

        private final Supplier<T> supplier;
        private T value;

        public CachedSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public T get() {
            return value == null ? value = supplier.get() : value;
        }

        public void invalidate() {
            this.value = null;
        }

    }

}
