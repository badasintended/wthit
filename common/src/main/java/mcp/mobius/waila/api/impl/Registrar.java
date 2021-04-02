package mcp.mobius.waila.api.impl;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public enum Registrar implements IRegistrar {

    INSTANCE;

    public final Map<Class<?>, List<IComponentProvider>> blockStack = new Object2ObjectOpenHashMap<>();
    public final Map<TooltipPosition, Map<Class<?>, List<IComponentProvider>>> blockComponent = new EnumMap<>(TooltipPosition.class);
    public final Map<Class<?>, List<IServerDataProvider<BlockEntity>>> blockData = new Object2ObjectOpenHashMap<>();

    public final Map<Class<?>, List<IEntityComponentProvider>> entityOverride = new Object2ObjectOpenHashMap<>();
    public final Map<Class<?>, List<IEntityComponentProvider>> entityStack = new Object2ObjectOpenHashMap<>();
    public final Map<TooltipPosition, Map<Class<?>, List<IEntityComponentProvider>>> entityComponent = new EnumMap<>(TooltipPosition.class);
    public final Map<Class<?>, List<IServerDataProvider<Entity>>> entityData = new Object2ObjectOpenHashMap<>();

    public final Map<Identifier, ITooltipRenderer> renderer = new Object2ObjectOpenHashMap<>();

    private boolean locked = false;
    private final TreeSet<Entry<?>> treeSet = new TreeSet<>(Comparator.comparingInt(Entry::getPriority));

    Registrar() {
        for (TooltipPosition position : TooltipPosition.values()) {
            blockComponent.put(position, new Object2ObjectOpenHashMap<>());
            entityComponent.put(position, new Object2ObjectOpenHashMap<>());
        }
    }

    @Override
    public void addConfig(Identifier key, boolean defaultValue) {
        assertLock();
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, false));
    }

    @Override
    public void addSyncedConfig(Identifier key, boolean defaultValue) {
        assertLock();
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, true));
    }

    @Override
    public <T> void registerStackProvider(int priority, IComponentProvider provider, Class<T> clazz) {
        registerProvider(priority, provider, clazz, blockStack);
    }

    @Override
    public <T> void registerComponentProvider(int priority, IComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        registerProvider(priority, provider, clazz, blockComponent.get(position));
    }

    @Override
    public <T> void registerBlockDataProvider(IServerDataProvider<BlockEntity> provider, Class<T> block) {
        registerProvider(0, provider, block, blockData);
    }

    @Override
    public <T> void registerOverrideEntityProvider(int priority, IEntityComponentProvider provider, Class<T> entity) {
        registerProvider(priority, provider, entity, entityOverride);
    }

    @Override
    public <T> void registerEntityStackProvider(int priority, IEntityComponentProvider provider, Class<T> entity) {
        registerProvider(priority, provider, entity, entityStack);
    }

    @Override
    public <T> void registerComponentProvider(int priority, IEntityComponentProvider provider, TooltipPosition position, Class<T> entity) {
        registerProvider(priority, provider, entity, entityComponent.get(position));
    }

    @Override
    public <T> void registerEntityDataProvider(IServerDataProvider<Entity> provider, Class<T> entity) {
        registerProvider(0, provider, entity, entityData);
    }

    @Override
    public void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer) {
        assertLock();
        this.renderer.put(id, renderer);
    }

    private <T, V extends Class<?>> void registerProvider(int priority, T provider, V clazz, Map<V, List<T>> target) {
        assertLock();

        if (clazz == null || provider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", provider.getClass().getName(), clazz));

        target.computeIfAbsent(clazz, c -> new List<>())
            .add(new Entry<>(priority, provider));
    }

    private void assertLock() {
        if (locked) {
            throw new IllegalStateException("Tried to register new component after the registrar is locked");
        }
    }

    public void lock() {
        locked = true;
    }

    public List<IComponentProvider> getBlockComponent(Object block, TooltipPosition position) {
        return get(block, blockComponent.get(position));
    }

    public List<IComponentProvider> getBlockStack(Object block) {
        return get(block, blockStack);
    }

    public List<IServerDataProvider<BlockEntity>> getBlockData(Object block) {
        return get(block, blockData);
    }

    public List<IEntityComponentProvider> getEntityComponent(Object entity, TooltipPosition position) {
        return get(entity, entityComponent.get(position));
    }

    public List<IEntityComponentProvider> getEntityOverride(Object entity) {
        return get(entity, entityOverride);
    }

    public List<IEntityComponentProvider> getEntityStack(Object entity) {
        return get(entity, entityStack);
    }

    public List<IServerDataProvider<Entity>> getEntityData(Object entity) {
        return get(entity, entityData);
    }

    public ITooltipRenderer getRenderer(Identifier id) {
        return renderer.get(id);
    }

    private <T> List<T> get(Object obj, Map<Class<?>, List<T>> map) {
        if (obj == null) {
            return List.EMPTY;
        }

        return get(obj.getClass(), map);
    }

    private <T> List<T> get(Class<?> clazz, Map<Class<?>, List<T>> map) {
        if (clazz == Object.class) {
            return List.EMPTY;
        }

        List<T> list;
        if (map.containsKey(clazz)) {
            list = map.get(clazz);
            if (!list.isFinal) {
                list.isFinal = true;

                treeSet.clear();
                treeSet.addAll(list);
                map.forEach((k, v) -> {
                    if (k != clazz && k.isAssignableFrom(clazz)) {
                        treeSet.addAll(v);
                    }
                });

                list.clear();
                treeSet.forEach(it -> list.add((Entry<T>) it));
            }
        } else {
            list = get(clazz.getSuperclass(), map);
            map.put(clazz, list);
        }
        return list;
    }

    public static class List<T> extends LinkedHashSet<Entry<T>> {

        static final List EMPTY = Util.make(new List<>(), s -> s.isFinal = true);

        boolean isFinal = false;

    }

    public static class Entry<T> {

        private final int priority;
        private final T value;

        Entry(int priority, T value) {
            this.priority = priority;
            this.value = value;
        }

        public T get() {
            return value;
        }

        int getPriority() {
            return priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Entry<?> entry = (Entry<?>) o;
            return value.equals(entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

    }

}
