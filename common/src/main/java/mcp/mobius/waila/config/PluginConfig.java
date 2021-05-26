package mcp.mobius.waila.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.util.Identifier;

public enum PluginConfig implements IPluginConfig {

    INSTANCE;

    static final Path PATH = Waila.configDir.resolve(WailaConstants.WAILA + "/" + WailaConstants.WAILA + "_plugins.json");
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<Identifier, ConfigEntry> configs = new HashMap<>();

    public void addConfig(ConfigEntry entry) {
        configs.put(entry.getId(), entry);
    }

    @Override
    public Set<Identifier> getKeys(String namespace) {
        return getKeys().stream().filter(id -> id.getNamespace().equals(namespace)).collect(Collectors.toSet());
    }

    @Override
    public Set<Identifier> getKeys() {
        return configs.keySet();
    }

    @Override
    public boolean get(Identifier key, boolean defaultValue) {
        ConfigEntry entry = configs.get(key);
        return entry == null ? defaultValue : entry.getValue();
    }

    public Set<ConfigEntry> getSyncableConfigs() {
        return configs.values().stream().filter(ConfigEntry::isSynced).collect(Collectors.toSet());
    }

    public List<String> getNamespaces() {
        return configs.keySet().stream().sorted((o1, o2) -> o1.getNamespace().equals(WailaConstants.WAILA)
            ? -1
            : o1.getNamespace().compareToIgnoreCase(o2.getNamespace()))
            .map(Identifier::getNamespace)
            .distinct()
            .collect(Collectors.toList());
    }

    public ConfigEntry getEntry(Identifier key) {
        return configs.get(key);
    }

    public void set(Identifier key, boolean value) {
        ConfigEntry entry = configs.get(key);
        if (entry != null) {
            entry.setValue(value);
        }
    }

    public void reload() {
        if (!Files.exists(PATH)) { // Write defaults, but don't read
            writeConfig(true);
        } else { // Read back from config
            Map<String, Map<String, Boolean>> config;
            try (BufferedReader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
                config = GSON.fromJson(reader, new TypeToken<Map<String, Map<String, Boolean>>>() {
                }.getType());
            } catch (IOException e) {
                config = Maps.newHashMap();
            }

            if (config == null) {
                writeConfig(true);
            } else {
                config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> set(new Identifier(namespace, path), value)));
            }
        }
    }

    public void save() {
        writeConfig(false);
    }

    private void writeConfig(boolean reset) {
        Map<String, Map<String, Boolean>> config = Maps.newHashMap();
        configs.values().forEach(e -> {
            Map<String, Boolean> modConfig = config.computeIfAbsent(e.getId().getNamespace(), k -> Maps.newHashMap());
            if (reset)
                e.setValue(e.getDefaultValue());
            modConfig.put(e.getId().getPath(), e.getValue());
        });

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
