package mcp.mobius.waila.gui.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.widget.ButtonEntry;
import mcp.mobius.waila.gui.widget.CategoryEntry;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import mcp.mobius.waila.gui.widget.value.IntInputValue;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PluginConfigScreen extends ConfigScreen {

    private static final String NO_CATEGORY = "no_category";
    private static final Map<ConfigEntry.Type<Object>, ConfigValueFunction<Object>> ENTRY_TO_VALUE = new HashMap<>();

    static {
        register(ConfigEntry.BOOLEAN, (key, name, value, defaultValue, save) -> new BooleanValue(name, value, defaultValue, save));
        register(ConfigEntry.INTEGER, (key, name, value, defaultValue, save) -> new IntInputValue(name, value, defaultValue, save, Registrar.INSTANCE.intConfigFormats.get(key)));
        register(ConfigEntry.DOUBLE, (key, name, value, defaultValue, save) -> new InputValue<>(name, value, defaultValue, save, InputValue.DECIMAL));
        register(ConfigEntry.STRING, (key, name, value, defaultValue, save) -> new InputValue<>(name, value, defaultValue, save, InputValue.ANY));
        register(ConfigEntry.ENUM, (key, name, value, defaultValue, save) -> new EnumValue(name, value.getDeclaringClass().getEnumConstants(), value, defaultValue, save));
    }

    private static final Map<ConfigEntry<Object>, Object> SYNCED_VALUES = new HashMap<>();

    public PluginConfigScreen(Screen parent) {
        super(parent, Component.translatable("gui.waila.plugin_settings"), PluginConfigScreen::saveAndRestore, PluginConfigScreen::reloadAndRestore);

        SYNCED_VALUES.clear();
        if (Minecraft.getInstance().getCurrentServer() != null) {
            PluginConfig.INSTANCE.getSyncableConfigs().forEach(entry -> SYNCED_VALUES.put(entry, entry.getValue()));
            PluginConfig.INSTANCE.reload();
        }
    }

    private static <T> void register(ConfigEntry.Type<T> type, ConfigValueFunction<T> function) {
        ENTRY_TO_VALUE.put((ConfigEntry.Type<Object>) type, (ConfigValueFunction<Object>) function);
    }

    private static void saveAndRestore() {
        PluginConfig.INSTANCE.save();
        SYNCED_VALUES.forEach(ConfigEntry::setValue);
    }

    private static void reloadAndRestore() {
        PluginConfig.INSTANCE.reload();
        SYNCED_VALUES.forEach(ConfigEntry::setValue);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ConfigListWidget getOptions() {
        ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26, PluginConfig.INSTANCE::save);
        for (String namespace : PluginConfig.INSTANCE.getNamespaces()) {
            String translationKey = "config.waila.plugin_" + namespace;
            Set<ResourceLocation> keys = PluginConfig.INSTANCE.getKeys(namespace);
            options.with(new ButtonEntry(translationKey, 100, 20, w -> minecraft.setScreen(new ConfigScreen(PluginConfigScreen.this, Component.translatable(translationKey)) {
                @Override
                public ConfigListWidget getOptions() {
                    ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26);
                    Object2IntMap<String> categories = new Object2IntLinkedOpenHashMap<>();
                    categories.put(NO_CATEGORY, 0);
                    for (ResourceLocation key : keys) {
                        ConfigEntry<Object> entry = PluginConfig.INSTANCE.getEntry(key);
                        String path = key.getPath();
                        String category = NO_CATEGORY;
                        if (path.contains(".")) {
                            String c = path.split("[.]", 2)[0];
                            String translation = translationKey + "." + c;
                            if (I18n.exists(translation)) {
                                category = c;
                                if (!categories.containsKey(category)) {
                                    options.with(new CategoryEntry(translation));
                                    categories.put(category, options.children().size());
                                }
                            }
                        }
                        int index = categories.getInt(category);
                        for (Object2IntMap.Entry<String> e : categories.object2IntEntrySet()) {
                            if (e.getIntValue() >= index) {
                                e.setValue(e.getIntValue() + 1);
                            }
                        }
                        ConfigValue<Object> value = ENTRY_TO_VALUE.get(entry.getType()).create(key, translationKey + "." + path, entry.getValue(), entry.getDefaultValue(), entry::setValue);
                        if (entry.isSynced() && minecraft.getCurrentServer() != null) {
                            value.serverOnly = true;
                            value.setValue(SYNCED_VALUES.get(entry));
                        }
                        options.with(index, value);
                    }
                    return options;
                }
            })));
        }
        return options;
    }

    @FunctionalInterface
    private interface ConfigValueFunction<T> {

        ConfigValue<T> create(ResourceLocation key, String name, T value, T defaultValue, Consumer<T> save);

    }

}
