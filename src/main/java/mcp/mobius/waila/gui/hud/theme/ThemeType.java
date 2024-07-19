package mcp.mobius.waila.gui.hud.theme;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ThemeType<T extends ITheme> implements IThemeType<T>, IThemeType.Builder<T> {

    private static final Set<String> INVALID_NAMES = Set.of("id", "type");
    private static final String INVALID_NAME_ERROR_MSG = "Theme type contains one of invalid property name [" + String.join(", ", INVALID_NAMES) + "]";

    public final Class<T> clazz;
    public final LinkedHashMap<String, Property<?, ?>> properties = new LinkedHashMap<>();

    private final MethodHandles.Lookup lookup;

    private boolean built = false;

    public ThemeType(Class<T> clazz) {
        this.clazz = clazz;

        try {
            this.lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
        } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public T create(Map<String, Object> attr) {
        try {
            var theme = clazz.getDeclaredConstructor().newInstance();
            properties.forEach((key, prop) ->
                prop.set(theme, TypeUtil.uncheckedCast(attr.computeIfAbsent(key, k -> Objects.requireNonNull(prop.defaultValue)))));
            return theme;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceLocation getId() {
        return Objects.requireNonNull(Registrar.get().themeTypes.inverse().get(this));
    }

    private <V, C> ThemeType<T> property(String name, Class<V> type, V exampleValue, @Nullable C context) {
        Preconditions.checkState(!built);
        Preconditions.checkArgument(!INVALID_NAMES.contains(name), INVALID_NAME_ERROR_MSG);
        properties.put(name, new Property<>(name, type, exampleValue, context));
        return this;
    }

    @Override
    public Builder<T> property(String name, int defaultValue) {
        return property(name, IntFormat.DECIMAL, defaultValue);
    }

    @Override
    public IThemeType.Builder<T> property(String name, IntFormat format, int defaultValue) {
        return property(name, int.class, defaultValue, format);
    }

    @Override
    public IThemeType.Builder<T> property(String name, boolean defaultValue) {
        return property(name, boolean.class, defaultValue, null);
    }

    @Override
    public IThemeType.Builder<T> property(String name, double defaultValue) {
        return property(name, double.class, defaultValue, null);
    }

    @Override
    public IThemeType.Builder<T> property(String name, String defaultValue) {
        return property(name, String.class, defaultValue, null);
    }

    @Override
    public <E extends Enum<E>> IThemeType.Builder<T> property(String name, E defaultValue) {
        return property(name, defaultValue.getDeclaringClass(), defaultValue, null);
    }

    @Override
    public IThemeType<T> build() {
        built = true;
        return this;
    }

    public final class Property<V, C> {

        public final String name;
        public final Class<V> type;
        public final V defaultValue;
        public final @Nullable C context;

        private final VarHandle handle;

        public Property(String name, Class<V> type, V defaultValue, @Nullable C context) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.context = context;

            try {
                this.handle = lookup.findVarHandle(clazz, name, type);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        public V get(Object theme) {
            return TypeUtil.uncheckedCast(handle.get(theme));
        }

        public void set(Object theme, Object value) {
            handle.set(theme, value);
        }

        public String getTlKey() {
            var typeId = getId();
            return "theme.waila.plugin_" + typeId.getNamespace() + "." + typeId.getPath() + "." + name;
        }

    }

}
