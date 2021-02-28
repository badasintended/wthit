package mcp.mobius.waila.api.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

public class WailaRegistrar implements IRegistrar {

    public static final WailaRegistrar INSTANCE = new WailaRegistrar();

    final Map<Class, List<IComponentProvider>> blockStackProviders;
    final EnumMap<TooltipPosition, Map<Class, List<IComponentProvider>>> blockComponentProviders;
    final Map<Class, List<IServerDataProvider<BlockEntity>>> blockDataProviders;

    final Map<Class, List<IEntityComponentProvider>> entityOverrideProviders;
    final Map<Class, List<IEntityComponentProvider>> entityStackProviders;
    final EnumMap<TooltipPosition, Map<Class, List<IEntityComponentProvider>>> entityComponentProviders;
    final Map<Class, List<IServerDataProvider<LivingEntity>>> entityDataProviders;

    final Map<Object, Map<Class, Map<Integer, ?>>> providers;

    final Map<Class, List<IBlockDecorator>> blockDecorators;
    final Map<Identifier, ITooltipRenderer> tooltipRenderers;

    WailaRegistrar() {
        blockStackProviders = new Object2ObjectLinkedOpenHashMap<>();
        blockComponentProviders = new EnumMap<>(TooltipPosition.class);
        blockDataProviders = new Object2ObjectLinkedOpenHashMap<>();

        entityOverrideProviders = new Object2ObjectLinkedOpenHashMap<>();
        entityStackProviders = new Object2ObjectLinkedOpenHashMap<>();
        entityComponentProviders = new EnumMap<>(TooltipPosition.class);
        entityDataProviders = new Object2ObjectLinkedOpenHashMap<>();

        providers = new Object2ObjectOpenHashMap<>();

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
        List<IBlockDecorator> decorators = blockDecorators.computeIfAbsent(block, b -> Lists.newArrayList());
        decorators.add(decorator);
    }

    @Override
    public void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer) {
        this.tooltipRenderers.put(id, renderer);
    }

    private <T, V extends Class<?>> void registerProvider(T dataProvider, V clazz, Map<V, List<T>> target) {
        if (clazz == null || dataProvider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));

        List<T> providers = target.computeIfAbsent(clazz, c -> Lists.newArrayList());
        if (providers.contains(dataProvider))
            return;

        providers.add(dataProvider);
    }

    /* PROVIDER GETTERS */

    public Map<Integer, List<IComponentProvider>> getHeadProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.HEAD));
    }

    public Map<Integer, List<IComponentProvider>> getBodyProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.BODY));
    }

    public Map<Integer, List<IComponentProvider>> getTailProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.TAIL));
    }

    public Map<Integer, List<IComponentProvider>> getStackProviders(Object block) {
        return getProviders(block, blockStackProviders);
    }

    public Map<Integer, List<IServerDataProvider<BlockEntity>>> getNBTProviders(Object block) {
        return getProviders(block, blockDataProviders);
    }

    public Map<Integer, List<IEntityComponentProvider>> getHeadEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.HEAD));
    }

    public Map<Integer, List<IEntityComponentProvider>> getBodyEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.BODY));
    }

    public Map<Integer, List<IEntityComponentProvider>> getTailEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.TAIL));
    }

    public Map<Integer, List<IEntityComponentProvider>> getOverrideEntityProviders(Object entity) {
        return getProviders(entity, entityOverrideProviders);
    }

    public Map<Integer, List<IEntityComponentProvider>> getStackEntityProviders(Object entity) {
        return getProviders(entity, entityStackProviders);
    }

    public Map<Integer, List<IServerDataProvider<LivingEntity>>> getNBTEntityProviders(Object entity) {
        return getProviders(entity, entityDataProviders);
    }

    public Map<Integer, List<IBlockDecorator>> getBlockDecorators(Object block) {
        return getProviders(block, blockDecorators);
    }

    public ITooltipRenderer getTooltipRenderer(Identifier id) {
        return this.tooltipRenderers.get(id);
    }

    private <T> Map<Integer, List<T>> getProviders(Object obj, Map<Class, List<T>> target) {
        return (Map<Integer, List<T>>) providers
            .computeIfAbsent(target, m -> new Object2ObjectOpenHashMap<>())
            .computeIfAbsent(obj.getClass(), o -> {
                Map<Integer, List<T>> returnList = new TreeMap<>();
                Integer index = 0;

                for (Class clazz : target.keySet()) {
                    if (clazz.isAssignableFrom(o))
                        returnList.put(index, target.get(clazz));

                    index++;
                }

                return returnList;
            });
    }

    /* HAS METHODS */

    public boolean hasStackProviders(Object block) {
        return hasProviders(block, blockStackProviders);
    }

    public boolean hasHeadProviders(Object block) {
        return hasProviders(block, blockComponentProviders.get(TooltipPosition.HEAD));
    }

    public boolean hasBodyProviders(Object block) {
        return hasProviders(block, blockComponentProviders.get(TooltipPosition.BODY));
    }

    public boolean hasTailProviders(Object block) {
        return hasProviders(block, blockComponentProviders.get(TooltipPosition.TAIL));
    }

    public boolean hasNBTProviders(Object block) {
        return hasProviders(block, blockDataProviders);
    }

    public boolean hasHeadEntityProviders(Object entity) {
        return hasProviders(entity, entityComponentProviders.get(TooltipPosition.HEAD));
    }

    public boolean hasBodyEntityProviders(Object entity) {
        return hasProviders(entity, entityComponentProviders.get(TooltipPosition.BODY));
    }

    public boolean hasTailEntityProviders(Object entity) {
        return hasProviders(entity, entityComponentProviders.get(TooltipPosition.TAIL));
    }

    public boolean hasOverrideEntityProviders(Object entity) {
        return hasProviders(entity, entityOverrideProviders);
    }

    public boolean hasStackEntityProviders(Object entity) {
        return hasProviders(entity, entityStackProviders);
    }

    public boolean hasNBTEntityProviders(Object entity) {
        return hasProviders(entity, entityDataProviders);
    }

    public boolean hasBlockDecorator(Object block) {
        return hasProviders(block, blockDecorators);
    }

    private <T> boolean hasProviders(Object obj, Map<Class, List<T>> target) {
        for (Class clazz : target.keySet())
            if (clazz.isInstance(obj))
                return true;
        return false;
    }

    @Deprecated
    public static WailaRegistrar instance() {
        return INSTANCE;
    }

}
