package mcp.mobius.waila.mcless.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.Gson;

public class ConfigIo<T> {

    private final Consumer<String> warn;
    private final BiConsumer<String, Throwable> error;
    private final Gson gson;
    private final Type type;
    private final Supplier<T> factory;
    private final int currentVersion;
    private final ToIntFunction<T> versionGetter;
    private final ObjIntConsumer<T> versionSetter;

    public ConfigIo(Consumer<String> warn, BiConsumer<String, Throwable> error, Gson gson, Type type, Supplier<T> factory, int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter) {
        this.warn = warn;
        this.error = error;
        this.gson = gson;
        this.type = type;
        this.factory = factory;
        this.currentVersion = currentVersion;
        this.versionGetter = versionGetter;
        this.versionSetter = versionSetter;
    }

    public ConfigIo(Consumer<String> warn, BiConsumer<String, Throwable> error, Gson gson, Type type, Supplier<T> factory) {
        this(warn, error, gson, type, factory, 0, t -> 0, (a, b) -> {});
    }

    public T read(Path path) {
        T config;
        boolean init = true;
        if (!Files.exists(path)) {
            Path parent = path.getParent();
            if (!Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    error.accept("Failed to make directory " + parent, e);
                }
            }
            config = factory.get();
        } else {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                config = gson.fromJson(reader, type);
                int version = versionGetter.applyAsInt(config);
                if (version != currentVersion) {
                    Path old = Paths.get(path + "_old");
                    warn.accept("Config file "
                        + path
                        + " contains different version ("
                        + version
                        + ") than required version ("
                        + currentVersion
                        + "), this config will be reset. Old config will be placed at "
                        + old);
                    Files.deleteIfExists(old);
                    Files.copy(path, old);
                    config = factory.get();
                } else {
                    init = false;
                }
            } catch (Exception e) {
                Path old = Paths.get(path + "_old");
                error.accept("Exception when reading config file "
                    + path
                    + ", this config will be reset. Old config will be placed at "
                    + old, e);
                try {
                    Files.deleteIfExists(old);
                    Files.copy(path, old);
                } catch (IOException e1) {
                    error.accept("well this is embarrassing...", e1);
                }
                config = factory.get();
            }
        }
        if (init) {
            versionSetter.accept(config, currentVersion);
            write(path, config);
        }
        return config;
    }

    public boolean write(Path path, T value) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(gson.toJson(value));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
