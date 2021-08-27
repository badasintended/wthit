package mcp.mobius.waila.config;

import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.gson.JsonPrimitive;
import mcp.mobius.waila.gui.widget.value.BooleanValue;
import mcp.mobius.waila.gui.widget.value.ConfigValue;
import mcp.mobius.waila.gui.widget.value.EnumValue;
import mcp.mobius.waila.gui.widget.value.InputValue;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigEntry<T> {

    public static final Type<Boolean> BOOLEAN = new Type<>() {
        @Override
        public Boolean parseValue(JsonPrimitive element, Boolean defaultValue) {
            return element.getAsBoolean();
        }

        @Override
        public ConfigValue<Boolean> createConfigValue(String name, Boolean value, Consumer<Boolean> consumer) {
            return new BooleanValue(name, value, consumer);
        }
    };

    public static final Type<Integer> INTEGER = new Type<>() {
        @Override
        public Integer parseValue(JsonPrimitive element, Integer defaultValue) {
            return element.getAsInt();
        }

        @Override
        public ConfigValue<Integer> createConfigValue(String name, Integer value, Consumer<Integer> consumer) {
            return new InputValue<>(name, value, consumer, InputValue.INTEGER);
        }
    };

    public static final Type<Double> DOUBLE = new Type<>() {
        @Override
        public Double parseValue(JsonPrimitive element, Double defaultValue) {
            return element.getAsDouble();
        }

        @Override
        public ConfigValue<Double> createConfigValue(String name, Double value, Consumer<Double> consumer) {
            return new InputValue<>(name, value, consumer, InputValue.DECIMAL);
        }
    };

    public static final Type<String> STRING = new Type<>() {
        @Override
        public String parseValue(JsonPrimitive element, String defaultValue) {
            return element.getAsString();
        }

        @Override
        public ConfigValue<String> createConfigValue(String name, String value, Consumer<String> consumer) {
            return new InputValue<>(name, value, consumer);
        }
    };

    public static final Type<Enum<? extends Enum>> ENUM = new Type<>() {
        @Override
        public Enum<?> parseValue(JsonPrimitive element, Enum<?> defaultValue) {
            return Enum.valueOf(defaultValue.getDeclaringClass(), element.getAsString());
        }

        @Override
        public ConfigValue<Enum<?>> createConfigValue(String name, Enum<?> value, Consumer<Enum<?>> consumer) {
            return new EnumValue(name, value.getDeclaringClass().getEnumConstants(), value, consumer);
        }
    };

    private final ResourceLocation id;
    private final T defaultValue;
    private final boolean synced;
    private final Type<T> type;
    private T value;

    private ConfigEntry(ResourceLocation id, T defaultValue, boolean synced, Type<T> type) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.synced = synced;
        this.type = type;
    }

    public Type<T> getType() {
        return type;
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
        Preconditions.checkArgument(
            value.getClass() == defaultValue.getClass(),
            "Tried to assign " + defaultValue.getClass() + " with " + value.getClass());
        this.value = value;
    }

    public interface Type<T> {

        T parseValue(JsonPrimitive element, T defaultValue);

        ConfigValue<T> createConfigValue(String name, T value, Consumer<T> consumer);

        default ConfigEntry<T> create(ResourceLocation id, T defaultValue, boolean synced) {
            return new ConfigEntry<>(id, defaultValue, synced, this);
        }

    }

}
