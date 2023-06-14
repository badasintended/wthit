package mcp.mobius.waila.config;

import java.nio.file.Path;
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
    public static final Type<Path> PATH = new Type<>((e, d) -> null, e -> null);

    private final ResourceLocation id;
    private final T defaultValue;
    private final T clientOnlyValue;
    private final boolean serverRequired;
    private final boolean merged;

    private final Type<T> type;

    private T serverValue;
    private T localValue;

    private ConfigEntry(ResourceLocation id, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged, Type<T> type) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.localValue = defaultValue;
        this.clientOnlyValue = clientOnlyValue;
        this.serverRequired = serverRequired;
        this.merged = merged;
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

    public boolean isServerRequired() {
        return serverRequired;
    }

    public T getLocalValue() {
        return localValue;
    }

    public T getServerValue() {
        return serverValue;
    }

    public void setLocalValue(T localValue) {
        assertInstance(localValue);
        this.localValue = localValue;
    }

    public void setServerValue(@Nullable T serverValue) {
        if (serverValue != null) {
            assertInstance(serverValue);
        }
        this.serverValue = serverValue;
    }

    @SuppressWarnings("unchecked")
    public T getValue(boolean forceLocal) {
        if (forceLocal) {
            return localValue;
        }

        if (merged && serverValue instanceof Boolean) {
            return (T) (Boolean.valueOf(serverValue == Boolean.TRUE && localValue == Boolean.TRUE));
        }

        return serverRequired ? Objects.requireNonNullElse(serverValue, clientOnlyValue) : localValue;
    }

    public boolean blocksClientEdit() {
        if (merged && serverValue instanceof Boolean) {
            return serverValue == Boolean.FALSE;
        }
        return serverRequired;
    }

    public boolean isMerged() {
        return merged;
    }

    public boolean isSynced() {
        return serverRequired || merged;
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

        public ConfigEntry<T> create(ResourceLocation id, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged) {
            return new ConfigEntry<>(id, defaultValue, clientOnlyValue, serverRequired, merged, this);
        }

    }

}
