package mcp.mobius.waila.api.impl.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PluginConfig implements IWailaConfigHandler {

    public static final PluginConfig INSTANCE = new PluginConfig();

    private final Map<Identifier, ConfigEntry> configs;

    private PluginConfig() {
        this.configs = Maps.newHashMap();
    }

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

    public Set<ConfigEntry> getSyncableConfigs() {
        return configs.values().stream().filter(ConfigEntry::isSynced).collect(Collectors.toSet());
    }

    @Override
    public boolean get(Identifier key, boolean defaultValue) {
        ConfigEntry entry = configs.get(key);
        return entry == null ? defaultValue : entry.getValue();
    }

    public void set(Identifier key, boolean value) {
        ConfigEntry entry = configs.computeIfAbsent(key, k -> new ConfigEntry(k, value, true));
        entry.setValue(value);
    }

    public void reload() {
        File configFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), Waila.MODID + "/" + Waila.MODID + "_plugins.json");

        if (!configFile.exists()) { // Write defaults, but don't read
            writeConfig(configFile, true);
        } else { // Read back from config
            Map<String, Map<String, Boolean>> config;
            try (FileReader reader = new FileReader(configFile)) {
                config = new Gson().fromJson(reader, new TypeToken<Map<String, Map<String, Boolean>>>(){}.getType());
            } catch (IOException e) {
                config = Maps.newHashMap();
            }

            config.forEach((namespace, subMap) -> subMap.forEach((path, value) -> set(new Identifier(namespace, path), value)));
        }
    }

    public void save() {
        File configFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), Waila.MODID + "/" + Waila.MODID + "_plugins.json");
        writeConfig(configFile, false);
    }

    private void writeConfig(File file, boolean reset) {
        Map<String, Map<String, Boolean>> config = Maps.newHashMap();
        configs.values().forEach(e -> {
            Map<String, Boolean> modConfig = config.computeIfAbsent(e.getId().getNamespace(), k -> Maps.newHashMap());
            if (reset)
                e.setValue(e.getDefaultValue());
            modConfig.put(e.getId().getPath(), e.getValue());
        });

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(config);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
