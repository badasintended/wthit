package mcp.mobius.waila.mcless.config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.Gson;
import mcp.mobius.waila.mcless.json5.Json5Writer;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

public class ConfigIo<T> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    private final Consumer<String> warn;
    private final BiConsumer<String, Throwable> error;
    private final boolean json5;
    private final Supplier<Function<List<String>, @Nullable String>> commenter;
    private final Gson gson;
    private final Type type;
    private final Supplier<T> factory;
    private final int currentVersion;
    private final ToIntFunction<T> versionGetter;
    private final ObjIntConsumer<T> versionSetter;

    public ConfigIo(Consumer<String> warn, BiConsumer<String, Throwable> error, boolean json5, Supplier<Function<List<String>, @Nullable String>> commenter, Gson gson, Type type, Supplier<T> factory, int currentVersion, ToIntFunction<T> versionGetter, ObjIntConsumer<T> versionSetter) {
        this.warn = warn;
        this.error = error;
        this.json5 = json5;
        this.commenter = commenter;
        this.gson = gson;
        this.type = type;
        this.factory = factory;
        this.currentVersion = currentVersion;
        this.versionGetter = versionGetter;
        this.versionSetter = versionSetter;
    }

    public ConfigIo(Consumer<String> warn, BiConsumer<String, Throwable> error, boolean json5, Supplier<Function<List<String>, @Nullable String>> commenter, Gson gson, Type type, Supplier<T> factory) {
        this(warn, error, json5, commenter, gson, type, factory, 0, t -> 0, (a, b) -> {});
    }

    public boolean migrateJson5(Path path) {
        if (!json5) return false;

        var pathString = path.toString();
        if (FilenameUtils.getExtension(pathString).equals("json5")) {
            var jsonPath = path.resolveSibling(FilenameUtils.getBaseName(pathString) + ".json");
            if (Files.exists(jsonPath)) try {
                Files.copy(jsonPath, path);
                Files.delete(jsonPath);
                warn.accept("Migrated from " + jsonPath + " to " + path);
                return true;
            } catch (IOException e) {
                error.accept("Failed to move " + jsonPath + " to " + path, e);
            }
        }
        return false;
    }

    public T read(Path path) {
        T config;
        var init = true;
        if (!Files.exists(path)) {
            if (migrateJson5(path)) return read(path);

            var parent = path.getParent();
            if (!Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    error.accept("Failed to make directory " + parent, e);
                }
            }
            config = factory.get();
        } else {
            try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                var jsonReader = gson.newJsonReader(reader);
                jsonReader.setLenient(true);
                config = gson.fromJson(jsonReader, type);
                var version = versionGetter.applyAsInt(config);
                if (version != currentVersion) {
                    var old = Paths.get(path + "_" + DATE_FORMAT.format(new Date()));
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
                var old = Paths.get(path + "_" + DATE_FORMAT.format(new Date()));
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

            config = read(path);
            write(path, config);
        }
        return config;
    }

    public boolean write(Path path, T value) {
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(value, type, json5 ? new Json5Writer(writer, commenter.get()) : gson.newJsonWriter(writer));
            return true;
        } catch (IOException e) {
            error.accept("Exception when writing config file " + path, e);
            return false;
        }
    }

}
