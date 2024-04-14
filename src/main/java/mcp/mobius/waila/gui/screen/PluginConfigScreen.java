package mcp.mobius.waila.gui.screen;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.buildconst.Tl;
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
import mcp.mobius.waila.network.common.VersionCommonPacket;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PluginConfigScreen extends ConfigScreen {

    private static final String NO_CATEGORY = "no_category";
    private static final Map<ConfigEntry.Type<Object>, ConfigValueFunction<Object>> ENTRY_TO_VALUE = new HashMap<>();

    static {
        register(ConfigEntry.BOOLEAN, (key, name, value, defaultValue, save) -> new BooleanValue(name, value, defaultValue, save));
        register(ConfigEntry.INTEGER, (key, name, value, defaultValue, save) -> new IntInputValue(name, value, defaultValue, save, Registrar.get().intConfigFormats.get(key)));
        register(ConfigEntry.DOUBLE, (key, name, value, defaultValue, save) -> new InputValue<>(name, value, defaultValue, save, InputValue.DECIMAL));
        register(ConfigEntry.STRING, (key, name, value, defaultValue, save) -> new InputValue<>(name, value, defaultValue, save, InputValue.ANY));

        //noinspection rawtypes,unchecked
        register(ConfigEntry.ENUM, (key, name, value, defaultValue, save) -> new EnumValue(name, value.getDeclaringClass().getEnumConstants(), value, defaultValue, save));
    }

    public PluginConfigScreen(Screen parent) {
        super(parent, Component.translatable(Tl.Gui.Plugin.SETTINGS), PluginConfig::save, PluginConfig::reload);
    }

    @SuppressWarnings("unchecked")
    private static <T> void register(ConfigEntry.Type<T> type, ConfigValueFunction<T> function) {
        ENTRY_TO_VALUE.put((ConfigEntry.Type<Object>) type, (ConfigValueFunction<Object>) function);
    }

    @Override
    public ConfigListWidget getOptions() {
        var options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26, PluginConfig::save);

        for (var namespace : PluginConfig.getNamespaces()) {
            var namespaceTlKey = Tl.Config.PLUGIN_ + namespace;
            var keys = PluginConfig.getAllKeys(namespace);
            if (keys.isEmpty()) continue;

            options.with(new ButtonEntry(namespaceTlKey, 100, 20, w -> minecraft.setScreen(new ConfigScreen(PluginConfigScreen.this,
                Component.translatable(Tl.Gui.Plugin.SETTINGS).append(" > ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable(namespaceTlKey).withStyle(ChatFormatting.WHITE))) {
                @Override
                public ConfigListWidget getOptions() {
                    var options = new ConfigListWidget(this, minecraft, width, height, 32, height - 32, 26);
                    Object2IntMap<String> categories = new Object2IntLinkedOpenHashMap<>();
                    categories.put(NO_CATEGORY, 0);

                    for (var key : keys) {
                        var entry = PluginConfig.getEntry(key);
                        var path = key.getPath();
                        var category = NO_CATEGORY;

                        if (path.contains(".")) {
                            var c = path.split("[.]", 2)[0];
                            var categoryTlKey = namespaceTlKey + "." + c;

                            if (I18n.exists(categoryTlKey)) {
                                category = c;

                                if (!categories.containsKey(category)) {
                                    options.with(new CategoryEntry(categoryTlKey));
                                    categories.put(category, options.children().size());
                                }
                            }
                        }

                        var index = categories.getInt(category);
                        var entryTlKey = namespaceTlKey + "." + path;

                        for (var e : categories.object2IntEntrySet()) {
                            if (e.getIntValue() >= index) {
                                e.setValue(e.getIntValue() + 1);
                            }
                        }

                        if (entry.getType().equals(ConfigEntry.PATH)) {
                            options.with(index, new ButtonEntry(entryTlKey, Tl.Config.OPEN_FILE, 100, 20, b ->
                                Util.getPlatform().openFile(((Path) entry.getDefaultValue()).toFile())));
                            continue;
                        }

                        var value = ENTRY_TO_VALUE.get(entry.getType()).create(key, entryTlKey, entry.getLocalValue(), entry.getDefaultValue(), entry::setLocalValue);
                        value.setId(key.toString());

                        if (entry.blocksClientEdit() && minecraft.getCurrentServer() != null) {
                            if (entry.getServerValue() == null) {
                                value.disable(PacketSender.c2s().canSend(VersionCommonPacket.TYPE)
                                    ? Tl.Config.SERVER_MISSING_OPTION
                                    : Tl.Config.SERVER_MISSING_MOD);
                                value.setValue(entry.getClientOnlyValue());
                            } else {
                                value.disable(entry.isMerged()
                                    ? Tl.Config.SERVER_DISABLED
                                    : Tl.Config.SERVER_ONLY);
                                value.setValue(entry.getServerValue());
                            }
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
