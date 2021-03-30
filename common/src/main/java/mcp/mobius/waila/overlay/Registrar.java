package mcp.mobius.waila.overlay;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcp.mobius.waila.api.IBlockDecorator;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

public enum Registrar implements IRegistrar {

    INSTANCE;

    // @formatter:off
    public final Map<Class<?>, Set<IComponentProvider>>                             blockStack      = new Object2ObjectLinkedOpenHashMap<>();
    public final Map<TooltipPosition, Map<Class<?>, Set<IComponentProvider>>>       blockComponent  = new EnumMap<>(TooltipPosition.class);
    public final Map<Class<?>, Set<IServerDataProvider<BlockEntity>>>               blockData       = new Object2ObjectLinkedOpenHashMap<>();

    public final Map<Class<?>, Set<IEntityComponentProvider>>                       entityOverride  = new Object2ObjectLinkedOpenHashMap<>();
    public final Map<Class<?>, Set<IEntityComponentProvider>>                       entityStack     = new Object2ObjectLinkedOpenHashMap<>();
    public final Map<TooltipPosition, Map<Class<?>, Set<IEntityComponentProvider>>> entityComponent = new EnumMap<>(TooltipPosition.class);
    public final Map<Class<?>, Set<IServerDataProvider<LivingEntity>>>              entityData      = new Object2ObjectLinkedOpenHashMap<>();

    public final Map<Identifier, ITooltipRenderer>                                  renderer        = new Object2ObjectLinkedOpenHashMap<>();
    // @formatter:on

    Registrar() {
        for (TooltipPosition position : TooltipPosition.values()) {
            blockComponent.put(position, new Object2ObjectLinkedOpenHashMap<>());
            entityComponent.put(position, new Object2ObjectLinkedOpenHashMap<>());
        }
    }

    @Override
    public void addConfig(Identifier key, boolean defaultValue) {
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, false));
    }

    @Override
    public void addSyncedConfig(Identifier key, boolean defaultValue) {
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, true));
    }

    @Override
    public <T> void registerStackProvider(IComponentProvider dataProvider, Class<T> block) {
        registerProvider(dataProvider, block, blockStack);
    }

    @Override
    public <T> void registerComponentProvider(IComponentProvider dataProvider, TooltipPosition position, Class<T> block) {
        registerProvider(dataProvider, block, blockComponent.get(position));
    }

    @Override
    public <T> void registerBlockDataProvider(IServerDataProvider<BlockEntity> dataProvider, Class<T> block) {
        registerProvider(dataProvider, block, blockData);
    }

    @Override
    public <T> void registerOverrideEntityProvider(IEntityComponentProvider dataProvider, Class<T> entity) {
        registerProvider(dataProvider, entity, entityOverride);
    }

    @Override
    public <T> void registerEntityStackProvider(IEntityComponentProvider dataProvider, Class<T> entity) {
        registerProvider(dataProvider, entity, entityStack);
    }

    @Override
    public <T> void registerComponentProvider(IEntityComponentProvider dataProvider, TooltipPosition position, Class<T> entity) {
        registerProvider(dataProvider, entity, entityComponent.get(position));
    }

    public <T> void registerEntityDataProvider(IServerDataProvider<LivingEntity> dataProvider, Class<T> entity) {
        registerProvider(dataProvider, entity, entityData);
    }

    @Override
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public <T> void registerDecorator(IBlockDecorator decorator, Class<T> block) {
    }

    @Override
    public void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer) {
        this.renderer.put(id, renderer);
    }

    private <T, V extends Class<?>> void registerProvider(T dataProvider, V clazz, Map<V, Set<T>> target) {
        if (clazz == null || dataProvider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));

        Set<T> providers = target.computeIfAbsent(clazz, c -> new Set<>());
        if (providers.contains(dataProvider))
            return;

        providers.add(dataProvider);
    }

    public LinkedHashSet<IComponentProvider> getBlockComponent(Object block, TooltipPosition position) {
        return get(block, blockComponent.get(position));
    }

    public LinkedHashSet<IComponentProvider> getBlockStack(Object block) {
        return get(block, blockStack);
    }

    public LinkedHashSet<IServerDataProvider<BlockEntity>> getBlockData(Object block) {
        return get(block, blockData);
    }

    public LinkedHashSet<IEntityComponentProvider> getEntityComponent(Object entity, TooltipPosition position) {
        return get(entity, entityComponent.get(position));
    }

    public LinkedHashSet<IEntityComponentProvider> getEntityOverride(Object entity) {
        return get(entity, entityOverride);
    }

    public LinkedHashSet<IEntityComponentProvider> getEntityStack(Object entity) {
        return get(entity, entityStack);
    }

    public LinkedHashSet<IServerDataProvider<LivingEntity>> getEntityData(Object entity) {
        return get(entity, entityData);
    }

    public ITooltipRenderer getRenderer(Identifier id) {
        return renderer.get(id);
    }

    private <T> LinkedHashSet<T> get(Object obj, Map<Class<?>, Set<T>> map) {
        if (obj == null) {
            return Set.EMPTY;
        }

        return get(obj.getClass(), map);
    }

    private <T> Set<T> get(Class<?> clazz, Map<Class<?>, Set<T>> map) {
        if (clazz == Object.class) {
            return Set.EMPTY;
        }

        Set<T> set;
        if (map.containsKey(clazz)) {
            set = map.get(clazz);
            if (!set.isFinal) {
                set.isFinal = true;
                map.forEach((k, v) -> {
                    if (k != clazz && k.isAssignableFrom(clazz)) {
                        set.addAll(v);
                    }
                });
            }
        } else {
            set = get(clazz.getSuperclass(), map);
            map.put(clazz, set);
        }
        return set;
    }

    static class Set<T> extends LinkedHashSet<T> {

        static final Set EMPTY = Util.make(new Set<>(), s -> s.isFinal = true);

        boolean isFinal = false;

    }

}
