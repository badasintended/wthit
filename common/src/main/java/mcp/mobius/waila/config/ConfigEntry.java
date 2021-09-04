package mcp.mobius.waila.config;

import com.google.common.base.Preconditions;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings({"rawtypes"})
public class ConfigEntry<T> {

    public static final Type<Boolean> BOOLEAN = (e, d) -> e.getAsBoolean();
    public static final Type<Integer> INTEGER = (e, d) -> e.getAsInt();
    public static final Type<Double> DOUBLE = (e, d) -> e.getAsDouble();
    public static final Type<String> STRING = (e, d) -> e.getAsString();
    public static final Type<Enum<? extends Enum>> ENUM = (e, d) -> Enum.valueOf(d.getDeclaringClass(), e.getAsString());

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

        default ConfigEntry<T> create(ResourceLocation id, T defaultValue, boolean synced) {
            return new ConfigEntry<>(id, defaultValue, synced, this);
        }

    }

}
