package mcp.mobius.waila.api.impl;

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
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class WailaRegistrar implements IRegistrar {

    public static final WailaRegistrar INSTANCE = new WailaRegistrar();

    final Map<Class, Set<IComponentProvider>> blockStackProviders;
    final EnumMap<TooltipPosition, Map<Class, Set<IComponentProvider>>> blockComponentProviders;
    final Map<Class, Set<IServerDataProvider<BlockEntity>>> blockDataProviders;

    final Map<Class, Set<IEntityComponentProvider>> entityOverrideProviders;
    final Map<Class, Set<IEntityComponentProvider>> entityStackProviders;
    final EnumMap<TooltipPosition, Map<Class, Set<IEntityComponentProvider>>> entityComponentProviders;
    final Map<Class, Set<IServerDataProvider<LivingEntity>>> entityDataProviders;

    final Map<Class, Set<IBlockDecorator>> blockDecorators;
    final Map<Identifier, ITooltipRenderer> tooltipRenderers;

    WailaRegistrar() {
        blockStackProviders = new Object2ObjectLinkedOpenHashMap<>();
        blockComponentProviders = new EnumMap<>(TooltipPosition.class);
        blockDataProviders = new Object2ObjectLinkedOpenHashMap<>();

        entityOverrideProviders = new Object2ObjectLinkedOpenHashMap<>();
        entityStackProviders = new Object2ObjectLinkedOpenHashMap<>();
        entityComponentProviders = new EnumMap<>(TooltipPosition.class);
        entityDataProviders = new Object2ObjectLinkedOpenHashMap<>();

        blockDecorators = new Object2ObjectLinkedOpenHashMap<>();
        tooltipRenderers = new Object2ObjectLinkedOpenHashMap<>();

        for (TooltipPosition position : TooltipPosition.values()) {
            blockComponentProviders.put(position, new Object2ObjectLinkedOpenHashMap<>());
            entityComponentProviders.put(position, new Object2ObjectLinkedOpenHashMap<>());
        }
    }

    /* CONFIG HANDLING */

    @Override
    public void addConfig(Identifier key, boolean defaultValue) {
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, false));
    }

    @Override
    public void addSyncedConfig(Identifier key, boolean defaultValue) {
        PluginConfig.INSTANCE.addConfig(new ConfigEntry(key, defaultValue, true));
    }

    /* REGISTRATION METHODS */

    @Override
    public void registerStackProvider(IComponentProvider dataProvider, Class block) {
        registerProvider(dataProvider, block, blockStackProviders);
    }

    @Override
    public void registerComponentProvider(IComponentProvider dataProvider, TooltipPosition position, Class block) {
        registerProvider(dataProvider, block, blockComponentProviders.get(position));
    }

    @Override
    public void registerBlockDataProvider(IServerDataProvider<BlockEntity> dataProvider, Class block) {
        registerProvider(dataProvider, block, blockDataProviders);
    }

    @Override
    public void registerOverrideEntityProvider(IEntityComponentProvider dataProvider, Class entity) {
        registerProvider(dataProvider, entity, entityOverrideProviders);
    }

    @Override
    public void registerEntityStackProvider(IEntityComponentProvider dataProvider, Class entity) {
        registerProvider(dataProvider, entity, entityStackProviders);
    }

    @Override
    public void registerComponentProvider(IEntityComponentProvider dataProvider, TooltipPosition position, Class entity) {
        registerProvider(dataProvider, entity, entityComponentProviders.get(position));
    }

    public void registerEntityDataProvider(IServerDataProvider<LivingEntity> dataProvider, Class entity) {
        registerProvider(dataProvider, entity, entityDataProviders);
    }

    @Override
    public void registerDecorator(IBlockDecorator decorator, Class block) {
        Set<IBlockDecorator> decorators = blockDecorators.computeIfAbsent(block, b -> new Set<>());
        decorators.add(decorator);
    }

    @Override
    public void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer) {
        this.tooltipRenderers.put(id, renderer);
    }

    private <T, V extends Class<?>> void registerProvider(T dataProvider, V clazz, Map<V, Set<T>> target) {
        if (clazz == null || dataProvider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));

        Set<T> providers = target.computeIfAbsent(clazz, c -> new Set<>());
        if (providers.contains(dataProvider))
            return;

        providers.add(dataProvider);
    }

    public LinkedHashSet<IComponentProvider> getBlockProviders(Object block, TooltipPosition position) {
        return getProviders(block, blockComponentProviders.get(position));
    }

    public LinkedHashSet<IComponentProvider> getStackProviders(Object block) {
        return getProviders(block, blockStackProviders);
    }

    public LinkedHashSet<IServerDataProvider<BlockEntity>> getNBTProviders(Object block) {
        return getProviders(block, blockDataProviders);
    }

    public LinkedHashSet<IEntityComponentProvider> getEntityProviders(Object entity, TooltipPosition position) {
        return getProviders(entity, entityComponentProviders.get(position));
    }

    public LinkedHashSet<IEntityComponentProvider> getOverrideEntityProviders(Object entity) {
        return getProviders(entity, entityOverrideProviders);
    }

    public LinkedHashSet<IEntityComponentProvider> getStackEntityProviders(Object entity) {
        return getProviders(entity, entityStackProviders);
    }

    public LinkedHashSet<IServerDataProvider<LivingEntity>> getNBTEntityProviders(Object entity) {
        return getProviders(entity, entityDataProviders);
    }

    public ITooltipRenderer getTooltipRenderer(Identifier id) {
        return this.tooltipRenderers.get(id);
    }

    private <T> LinkedHashSet<T> getProviders(Object obj, Map<Class, Set<T>> target) {
        Set<T> set = target.computeIfAbsent(obj.getClass(), c -> new Set<>());

        if (!set.isFinal) {
            for (Class clazz : target.keySet()) {
                if (clazz.isAssignableFrom(obj.getClass())) {
                    set.addAll(target.get(clazz));
                }
            }
            set.isFinal = true;
        }

        if (set != Set.EMPTY && set.isEmpty()) {
            target.put(obj.getClass(), Set.EMPTY);
            return Set.EMPTY;
        }

        return set;
    }

    static class Set<T> extends LinkedHashSet<T> {

        static final Set EMPTY = Util.make(new Set<>(), s -> s.isFinal = true);

        boolean isFinal = false;

    }

}
