package mcp.mobius.waila.config;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import mcp.mobius.waila.api.IPluginInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"rawtypes"})
public class ConfigEntry<T> {

    public static final Type<Boolean> BOOLEAN = new Type<>((e, d) -> e.getAsBoolean(), JsonPrimitive::new);
    public static final Type<Integer> INTEGER = new Type<>((e, d) -> e.getAsInt(), JsonPrimitive::new);
    public static final Type<Double> DOUBLE = new Type<>((e, d) -> e.getAsDouble(), JsonPrimitive::new);
    public static final Type<String> STRING = new Type<>((e, d) -> e.getAsString(), JsonPrimitive::new);
    public static final Type<Enum<? extends Enum>> ENUM = new Type<>((e, d) -> Enum.valueOf(d.getDeclaringClass(), e.getAsString()), e -> new JsonPrimitive(e.name()));
    public static final Type<Path> PATH = new Type<>((e, d) -> d, e -> JsonNull.INSTANCE);

    private final IPluginInfo origin;
    private final ResourceLocation id;
    private final T defaultValue;
    private final T clientOnlyValue;
    private final boolean serverRequired;
    private final boolean merged;

    private final Type<T> type;

    private @Nullable T serverValue;
    private T localValue;

    private ConfigEntry(IPluginInfo origin, ResourceLocation id, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged, Type<T> type) {
        this.origin = origin;
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

    public IPluginInfo getOrigin() {
        return origin;
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

    public @Nullable T getServerValue() {
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

    public ConfigEntry<T> getActual() {
        return this;
    }

    public Alias<T> createAlias(ResourceLocation id) {
        return new Alias<>(id, getActual());
    }

    public boolean isAlias() {
        return false;
    }

    private void assertInstance(T value) {
        Preconditions.checkArgument(
            value.getClass() == defaultValue.getClass(),
            "Tried to assign " + defaultValue.getClass() + " with " + value.getClass());
    }

    public static class Type<T> {

        public final BiFunction<JsonElement, T, T> parser;
        public final Function<T, JsonElement> serializer;

        public Type(BiFunction<JsonElement, T, T> parser, Function<T, JsonElement> serializer) {
            this.parser = parser;
            this.serializer = serializer;
        }

        public ConfigEntry<T> create(IPluginInfo origin, ResourceLocation id, T defaultValue, T clientOnlyValue, boolean serverRequired, boolean merged) {
            return new ConfigEntry<>(origin, id, defaultValue, clientOnlyValue, serverRequired, merged, this);
        }

    }

    public static class Alias<T> extends ConfigEntry<T> {

        public final ConfigEntry<T> delegate;

        private Alias(ResourceLocation id, ConfigEntry<T> delegate) {
            super(delegate.origin, id, delegate.defaultValue, delegate.clientOnlyValue, delegate.serverRequired, delegate.merged, delegate.type);
            this.delegate = delegate;
        }

        @Override
        public T getServerValue() {
            return delegate.getServerValue();
        }

        @Override
        public void setServerValue(@Nullable T serverValue) {
            delegate.setServerValue(serverValue);
        }

        @Override
        public T getLocalValue() {
            return delegate.getLocalValue();
        }

        @Override
        public void setLocalValue(T localValue) {
            delegate.setLocalValue(localValue);
        }

        @Override
        public ConfigEntry<T> getActual() {
            return delegate.getActual();
        }

        @Override
        public boolean isAlias() {
            return true;
        }

    }

}
