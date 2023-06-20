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
import mcp.mobius.waila.util.Log;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unchecked")
public enum PluginConfig implements IPluginConfig {

    CLIENT, SERVER;

    private static final Log LOG = Log.create();

    private static final Path PATH = Waila.CONFIG_DIR.resolve(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA + "_plugins.json");
    private static final ConfigIo<Map<String, Map<String, JsonPrimitive>>> IO = new ConfigIo<>(
        LOG::warn, LOG::error,
        new GsonBuilder().setPrettyPrinting().create(),
        new TypeToken<Map<String, Map<String, JsonPrimitive>>>() {
        }.getType(),
        LinkedHashMap::new);

    public static final Map<ResourceLocation, ConfigEntry<?>> CONFIGS = new LinkedHashMap<>();

    public static <T> void addConfig(ConfigEntry<T> entry) {
        CONFIGS.put(entry.getId(), entry);
    }

    public static Set<ResourceLocation> getAllKeys(String namespace) {
        return getAllKeys().stream()
            .filter(id -> id.getNamespace().equals(namespace))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<ResourceLocation> getAllKeys() {
        return CONFIGS.keySet();
    }

    public static Set<ConfigEntry<Object>> getSyncableConfigs() {
        return CONFIGS.values().stream()
            .filter(ConfigEntry::isSynced)
            .map(t -> (ConfigEntry<Object>) t)
            .collect(Collectors.toSet());
    }

    public static List<String> getNamespaces() {
        return CONFIGS.keySet().stream()
            .map(ResourceLocation::getNamespace)
            .distinct()
            .sorted((o1, o2) -> o1.equals(WailaConstants.NAMESPACE) ? -1 : o2.equals(WailaConstants.NAMESPACE) ? 1 : o1.compareToIgnoreCase(o2))
            .collect(Collectors.toList());
    }

    public static <T> ConfigEntry<T> getEntry(ResourceLocation key) {
        return (ConfigEntry<T>) CONFIGS.get(key);
    }

    public static <T> void set(ResourceLocation key, T value) {
        ConfigEntry<T> entry = (ConfigEntry<T>) CONFIGS.get(key);
        if (entry != null) {
            entry.setLocalValue(value);
        }
    }

    public static void reload() {
        if (!Files.exists(PATH)) {
            writeConfig();
        }
        Map<String, Map<String, JsonPrimitive>> config = IO.read(PATH);
        config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) CONFIGS.get(new ResourceLocation(namespace, path));
            if (entry != null) {
                entry.setLocalValue(entry.getType().parser.apply(value, entry.getDefaultValue()));
            }
        }));
        LOG.info("Plugin config reloaded");
    }

    public static void save() {
        writeConfig();
    }

    private static void writeConfig() {
        Map<String, Map<String, JsonPrimitive>> config = new LinkedHashMap<>();
        for (ConfigEntry<?> e : CONFIGS.values()) {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) e;
            ResourceLocation id = entry.getId();
            config.computeIfAbsent(id.getNamespace(), k -> new LinkedHashMap<>())
                .put(id.getPath(), entry.getType().serializer.apply(entry.getLocalValue()));
        }

        IO.write(PATH, config);
    }

    private <T> T getValue(ResourceLocation key, T defaultValue) {
        return CONFIGS.containsKey(key)
            ? (T) CONFIGS.get(key).getValue(this == SERVER)
            : defaultValue;
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
        return getValue(key, false);
    }

    @Override
    public int getInt(ResourceLocation key) {
        return getValue(key, 0);
    }

    @Override
    public double getDouble(ResourceLocation key) {
        return getValue(key, 0);
    }

    @Override
    public String getString(ResourceLocation key) {
        return getValue(key, "");
    }

    @Override
    public <T extends Enum<T>> T getEnum(ResourceLocation key) {
        return getValue(key, null);
    }

}
