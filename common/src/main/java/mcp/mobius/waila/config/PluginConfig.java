package mcp.mobius.waila.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.util.CommonUtil;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unchecked")
public enum PluginConfig implements IPluginConfig {

    INSTANCE;

    private static final Path PATH = CommonUtil.configDir.resolve(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA + "_plugins.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final TypeToken<Map<String, Map<String, JsonPrimitive>>> TYPE_TOKEN = new TypeToken<>() {
    };

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
        if (!Files.exists(PATH)) { // Write defaults, but don't read
            writeConfig(true);
        } else { // Read back from config
            Map<String, Map<String, JsonPrimitive>> config;
            try (BufferedReader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, TYPE_TOKEN.getType());
            } catch (IOException e) {
                config = new HashMap<>();
            }

            if (config == null) {
                writeConfig(true);
            } else {
                config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> {
                    ConfigEntry<Object> entry = (ConfigEntry<Object>) configs.get(new ResourceLocation(namespace, path));
                    if (entry != null) {
                        entry.setValue(entry.getType().parseValue(value, entry.getDefaultValue()));
                    }
                }));
            }
        }
    }

    public void save() {
        writeConfig(false);
    }

    private <T> T getValue(ResourceLocation key) {
        return (T) configs.get(key).getValue();
    }

    private void writeConfig(boolean reset) {
        Map<String, Map<String, Object>> config = new HashMap<>();
        for (ConfigEntry<?> e : configs.values()) {
            ConfigEntry<Object> entry = (ConfigEntry<Object>) e;
            Map<String, Object> modConfig = config.computeIfAbsent(entry.getId().getNamespace(), k -> new HashMap<>());
            if (reset) {
                entry.setValue(entry.getDefaultValue());
            }

            Object value = entry.getValue();
            if (value instanceof Enum<?> enumValue) {
                modConfig.put(entry.getId().getPath(), enumValue.name());
            } else {
                modConfig.put(entry.getId().getPath(), entry.getValue());
            }
        }

        try {
            String json = GSON.toJson(config);
            Path parent = PATH.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            BufferedWriter writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
