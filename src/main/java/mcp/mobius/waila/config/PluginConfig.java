package mcp.mobius.waila.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.mcless.config.ConfigIo;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unchecked")
public enum PluginConfig implements IPluginConfig {

    CLIENT, SERVER;

    private static final Path path = Waila.CONFIG_DIR.resolve(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA + "_plugins.json");
    private static final ConfigIo<Map<String, Map<String, JsonPrimitive>>> io = new ConfigIo<>(
        Waila.LOGGER::warn, Waila.LOGGER::error,
        new GsonBuilder().setPrettyPrinting().create(),
        new TypeToken<Map<String, Map<String, JsonPrimitive>>>() {
        }.getType(),
        LinkedHashMap::new);

    public static final Map<ResourceLocation, ConfigEntry<?>> configs = new LinkedHashMap<>();

    public static <T> ConfigEntry<T> addConfig(ConfigEntry<T> entry) {
        configs.put(entry.getId(), entry);
        return entry;
    }

    public static Set<ResourceLocation> getAllKeys(String namespace) {
        return getAllKeys().stream()
            .filter(id -> id.getNamespace().equals(namespace))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<ResourceLocation> getAllKeys() {
        return configs.keySet();
    }

    public static Set<ConfigEntry<Object>> getSyncableConfigs() {
        return configs.values().stream()
            .filter(ConfigEntry::isSynced)
            .map(t -> (ConfigEntry<Object>) t)
            .collect(Collectors.toSet());
    }

    public static List<String> getNamespaces() {
        return configs.keySet().stream()
            .map(ResourceLocation::getNamespace)
            .distinct()
            .sorted((o1, o2) -> o1.equals(WailaConstants.NAMESPACE) ? -1 : o2.equals(WailaConstants.NAMESPACE) ? 1 : o1.compareToIgnoreCase(o2))
            .collect(Collectors.toList());
    }

    public static <T> ConfigEntry<T> getEntry(ResourceLocation key) {
        return (ConfigEntry<T>) configs.get(key);
    }

    public static <T> void set(ResourceLocation key, T value) {
        ConfigEntry<T> entry = (ConfigEntry<T>) configs.get(key);
        if (entry != null) {
            entry.setLocalValue(value);
        }
    }

    public static void reload() {
        if (!Files.exists(path)) {
            writeConfig();
        }
        Map<String, Map<String, JsonPrimitive>> config = io.read(path);
        config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) configs.get(new ResourceLocation(namespace, path));
            if (entry != null) {
                entry.setLocalValue(entry.getType().parser.apply(value, entry.getDefaultValue()));
            }
        }));
        Waila.LOGGER.info("Plugin config reloaded");
    }

    public static void save() {
        writeConfig();
    }

    private static void writeConfig() {
        Map<String, Map<String, JsonPrimitive>> config = new LinkedHashMap<>();
        for (ConfigEntry<?> e : configs.values()) {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) e;
            ResourceLocation id = entry.getId();
            config.computeIfAbsent(id.getNamespace(), k -> new LinkedHashMap<>())
                .put(id.getPath(), entry.getType().serializer.apply(entry.getLocalValue()));
        }

        io.write(path, config);
    }

    private <T> T getValue(ResourceLocation key) {
        return (T) configs.get(key).getValue(this == SERVER);
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return getAllKeys();
    }

    @Override
    public Set<ResourceLocation> getKeys(String namespace) {
        return getAllKeys(namespace);
    }

    @Override
    public boolean getBoolean(ResourceLocation key) {
        return getValue(key);
    }

    @Override
    public int getInt(ResourceLocation key) {
        return getValue(key);
    }

    @Override
    public double getDouble(ResourceLocation key) {
        return getValue(key);
    }

    @Override
    public String getString(ResourceLocation key) {
        return getValue(key);
    }

    @Override
    public <T extends Enum<T>> T getEnum(ResourceLocation key) {
        return getValue(key);
    }

}
