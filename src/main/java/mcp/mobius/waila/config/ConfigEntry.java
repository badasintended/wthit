package mcp.mobius.waila.config;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"rawtypes"})
public class ConfigEntry<T> {

    public static final Type<Boolean> BOOLEAN = new Type<>((e, d) -> e.getAsBoolean(), JsonPrimitive::new);
    public static final Type<Integer> INTEGER = new Type<>((e, d) -> e.getAsInt(), JsonPrimitive::new);
    public static final Type<Double> DOUBLE = new Type<>((e, d) -> e.getAsDouble(), JsonPrimitive::new);
    public static final Type<String> STRING = new Type<>((e, d) -> e.getAsString(), JsonPrimitive::new);
    public static final Type<Enum<? extends Enum>> ENUM = new Type<>((e, d) -> Enum.valueOf(d.getDeclaringClass(), e.getAsString()), e -> new JsonPrimitive(e.name()));

    private final ResourceLocation id;
    private final T defaultValue;
    private final T clientOnlyValue;
    private final boolean synced;
    private final Type<T> type;

    private T syncedValue;
    private T localValue;

    private boolean merged = false;

    private ConfigEntry(ResourceLocation id, T defaultValue, T clientOnlyValue, boolean synced, Type<T> type) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.localValue = defaultValue;
        this.clientOnlyValue = clientOnlyValue;
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

    public T getClientOnlyValue() {
        return clientOnlyValue;
    }

    public boolean isSynced() {
        return synced;
    }

    public T getLocalValue() {
        return localValue;
    }

    public T getSyncedValue() {
        return syncedValue;
    }

    public void setLocalValue(T localValue) {
        assertInstance(localValue);
        this.localValue = localValue;
    }

    public void setSyncedValue(@Nullable T syncedValue) {
        if (syncedValue != null) {
            assertInstance(syncedValue);
        }
        this.syncedValue = syncedValue;
    }

    @SuppressWarnings("unchecked")
    public T getValue(boolean forceLocal) {
        if (forceLocal) {
            return localValue;
        }

        if (merged && syncedValue instanceof Boolean) {
            return (T) (Boolean.valueOf(syncedValue == Boolean.TRUE && localValue == Boolean.TRUE));
        }

        return synced ? Objects.requireNonNullElse(syncedValue, clientOnlyValue) : localValue;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public boolean blocksClientEdit() {
        return synced && !(merged && syncedValue == Boolean.TRUE);
    }

    public boolean isMerged() {
        return merged;
    }

    private void assertInstance(T value) {
        Preconditions.checkArgument(
            value.getClass() == defaultValue.getClass(),
            "Tried to assign " + defaultValue.getClass() + " with " + value.getClass());
    }

    public static class Type<T> {

        public final BiFunction<JsonPrimitive, T, T> parser;
        public final Function<T, JsonPrimitive> serializer;

        public Type(BiFunction<JsonPrimitive, T, T> parser, Function<T, JsonPrimitive> serializer) {
            this.parser = parser;
            this.serializer = serializer;
        }

        public ConfigEntry<T> create(ResourceLocation id, T defaultValue, T clientOnlyValue, boolean synced) {
            return new ConfigEntry<>(id, defaultValue, clientOnlyValue, synced, this);
        }

    }

}
