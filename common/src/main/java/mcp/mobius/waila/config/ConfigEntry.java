package mcp.mobius.waila.config;

import java.util.function.Consumer;

import mcp.mobius.waila.gui.widget.value.ConfigValue;
import net.minecraft.resources.ResourceLocation;

public class ConfigEntry<T> {

    private final ResourceLocation id;
    private final T defaultValue;
    private final boolean synced;
    private final ConfigValueFactory<T> factory;
    private T value;

    public ConfigEntry(ResourceLocation id, T defaultValue, boolean synced, ConfigValueFactory<T> factory) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.synced = synced;
        this.factory = factory;
    }

    public ConfigValueFactory<T> getFactory() {
        return factory;
    }

    public ResourceLocation getId() {
        return id;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isSynced() {
        return synced;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @FunctionalInterface
    public interface ConfigValueFactory<T> {

        ConfigValue<T> accept(String name, T defaultValue, Consumer<T> consumer);

    }

}
