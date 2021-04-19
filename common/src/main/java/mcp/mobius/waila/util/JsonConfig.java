package mcp.mobius.waila.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IJsonConfig;

public class JsonConfig<T> implements IJsonConfig<T> {

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File configFile;
    private final CachedSupplier<T> configGetter;
    private final Gson gson;

    JsonConfig(File file, Class<T> clazz, Supplier<T> factory, Gson gson) {
        this.configFile = file;
        this.configGetter = new CachedSupplier<>(() -> {
            if (!configFile.exists()) {
                T def = factory.get();
                write(def, false);
                return def;
            }
            try (FileReader reader = new FileReader(configFile)) {
                return gson.fromJson(reader, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return factory.get();
        });
        this.gson = gson;
    }

    @Override
    public T get() {
        return configGetter.get();
    }

    @Override
    public void save() {
        write(get(), false); // Does not need to invalidate since the saved instance already has updated values
    }

    @Override
    public void write(T t, boolean invalidate) {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(t));
            if (invalidate)
                invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void invalidate() {
        configGetter.invalidate();
    }

    private static <T> Supplier<T> defaultFactory(Class<T> configClass) {
        return () -> {
            try {
                return configClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create new config instance", e);
            }
        };
    }

    public static class Builder<T> implements Builder0<T>, Builder1<T> {

        final Class<T> clazz;
        File file;
        Supplier<T> factory;
        Gson gson;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
            this.gson = DEFAULT_GSON;
            this.factory = () -> {
                try {
                    return clazz.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create new config instance", e);
                }
            };
        }

        @Override
        public Builder1<T> file(File file) {
            this.file = file;
            return this;
        }

        @Override
        public Builder1<T> file(String fileName) {
            this.file = Waila.configDir.resolve(fileName + (fileName.endsWith(".json") ? "" : ".json")).toFile();
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
            return new JsonConfig<T>(file, clazz, factory, gson);
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
