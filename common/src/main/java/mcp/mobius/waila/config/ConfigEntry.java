package mcp.mobius.waila.config;

import java.util.function.Consumer;

import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigEntry<T> {

    public static final ConfigValueFactory<Boolean> BOOLEAN = BooleanValue::new;
    public static final ConfigValueFactory<Integer> INTEGER = (n, d, s) -> new InputValue<>(n, d, s, InputValue.INTEGER);
    public static final ConfigValueFactory<Double> DOUBLE = (n, d, s) -> new InputValue<>(n, d, s, InputValue.DECIMAL);
    public static final ConfigValueFactory<String> STRING = InputValue::new;
    public static final ConfigValueFactory<Enum<?>> ENUM =  (n, d, s) -> new EnumValue(n, d.getDeclaringClass().getEnumConstants(), d, s);


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
