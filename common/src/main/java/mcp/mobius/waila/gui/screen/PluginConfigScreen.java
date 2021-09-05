package mcp.mobius.waila.gui.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.widget.ConfigListWidget;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PluginConfigScreen extends ConfigScreen {

    private static final String NO_CATEGORY = "no_category";
    private static final Map<ConfigEntry.Type<Object>, ConfigValueFunction<Object>> ENTRY_TO_VALUE = new HashMap<>();

    static {
        register(ConfigEntry.BOOLEAN, BooleanValue::new);
        register(ConfigEntry.INTEGER, (name, value, save) -> new InputValue<>(name, value, save, InputValue.INTEGER));
        register(ConfigEntry.DOUBLE, (name, value, save) -> new InputValue<>(name, value, save, InputValue.DECIMAL));
        register(ConfigEntry.STRING, InputValue::new);
        register(ConfigEntry.ENUM, (name, value, save) -> new EnumValue(name, value.getDeclaringClass().getEnumConstants(), value, save));
    }

    public PluginConfigScreen(Screen parent) {
        super(parent, new TranslatableComponent("gui.waila.plugin_settings"), PluginConfig.INSTANCE::save, PluginConfig.INSTANCE::reload);
    }

    private static <T> void register(ConfigEntry.Type<T> type, ConfigValueFunction<T> function) {
        ENTRY_TO_VALUE.put((ConfigEntry.Type<Object>) type, (ConfigValueFunction<Object>) function);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ConfigListWidget getOptions() {
        ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30, PluginConfig.INSTANCE::save);
        for (String namespace : PluginConfig.INSTANCE.getNamespaces()) {
            String translationKey = "config.waila.plugin_" + namespace;
            Set<ResourceLocation> keys = PluginConfig.INSTANCE.getKeys(namespace);
            options.withButton(translationKey, 100, 20, w -> minecraft.setScreen(new ConfigScreen(PluginConfigScreen.this, new TranslatableComponent(translationKey)) {
                @Override
                public ConfigListWidget getOptions() {
                    ConfigListWidget options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 30);
                    Object2IntMap<String> categories = new Object2IntLinkedOpenHashMap<>();
                    categories.put(NO_CATEGORY, 0);
                    for (ResourceLocation key : keys) {
                        ConfigEntry<Object> entry = PluginConfig.INSTANCE.getEntry(key);
                        if (entry.isSynced() && Minecraft.getInstance().getCurrentServer() != null) {
                            continue;
                        }
                        String path = key.getPath();
                        String category = NO_CATEGORY;
                        if (path.contains(".")) {
                            String c = path.split("[.]", 2)[0];
                            String translation = translationKey + "." + c;
                            if (I18n.exists(translation)) {
                                category = c;
                                if (!categories.containsKey(category)) {
                                    options.withCategory(translation);
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
                        options.with(index, ENTRY_TO_VALUE.get(entry.getType()).create(translationKey + "." + path, entry.getValue(), entry::setValue));
                    }
                    return options;
                }
            }));
        }
        return options;
    }

    @FunctionalInterface
    private interface ConfigValueFunction<T> {

        ConfigValue<T> create(String name, T value, Consumer<T> save);

    }

}
