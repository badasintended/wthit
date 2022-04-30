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

    INSTANCE;

    private final Path path = Waila.CONFIG_DIR.resolve(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA + "_plugins.json");
    private final ConfigIo<Map<String, Map<String, JsonPrimitive>>> io = new ConfigIo<>(
        Waila.LOGGER::warn, Waila.LOGGER::error,
        new GsonBuilder().setPrettyPrinting().create(),
        new TypeToken<Map<String, Map<String, JsonPrimitive>>>() {
        }.getType(),
        LinkedHashMap::new);

    private final Map<ResourceLocation, ConfigEntry<?>> configs = new LinkedHashMap<>();

    public void addConfig(ConfigEntry<?> entry) {
        configs.put(entry.getId(), entry);
    }

    @Override
    public Set<ResourceLocation> getKeys(String namespace) {
        return getKeys().stream()
            .filter(id -> id.getNamespace().equals(namespace))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ResourceLocation> getKeys() {
        return configs.keySet();
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

    @Override
    public boolean get(ResourceLocation key, boolean defaultValue) {
        ConfigEntry<?> entry = configs.get(key);
        return entry == null ? defaultValue : (boolean) entry.getValue();
    }

    public Set<ConfigEntry<Object>> getSyncableConfigs() {
        return configs.values().stream()
            .filter(ConfigEntry::isSynced)
            .map(t -> (ConfigEntry<Object>) t)
            .collect(Collectors.toSet());
    }

    public List<String> getNamespaces() {
        return configs.keySet().stream()
            .map(ResourceLocation::getNamespace)
            .distinct()
            .sorted((o1, o2) -> o1.equals(WailaConstants.NAMESPACE) ? -1 : o2.equals(WailaConstants.NAMESPACE) ? 1 : o1.compareToIgnoreCase(o2))
            .collect(Collectors.toList());
    }

    public <T> ConfigEntry<T> getEntry(ResourceLocation key) {
        return (ConfigEntry<T>) configs.get(key);
    }

    public <T> void set(ResourceLocation key, T value) {
        ConfigEntry<T> entry = (ConfigEntry<T>) configs.get(key);
        if (entry != null) {
            entry.setValue(value);
        }
    }

    public void reload() {
        if (!Files.exists(path)) {
            writeConfig();
        }
        Map<String, Map<String, JsonPrimitive>> config = io.read(path);
        config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) configs.get(new ResourceLocation(namespace, path));
            if (entry != null) {
                entry.setValue(entry.getType().parser.apply(value, entry.getDefaultValue()));
            }
        }));
        Waila.LOGGER.info("Plugin config reloaded");
    }

    public void save() {
        writeConfig();
    }

    private <T> T getValue(ResourceLocation key) {
        return (T) configs.get(key).getValue();
    }

    private void writeConfig() {
        Map<String, Map<String, JsonPrimitive>> config = new LinkedHashMap<>();
        for (ConfigEntry<?> e : configs.values()) {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) e;
            ResourceLocation id = entry.getId();
            config.computeIfAbsent(id.getNamespace(), k -> new LinkedHashMap<>())
                .put(id.getPath(), entry.getType().serializer.apply(entry.getValue()));
        }

        io.write(path, config);
    }

}
