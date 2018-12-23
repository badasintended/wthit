package mcp.mobius.waila.api.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class WailaRegistrar implements IWailaRegistrar {

    public static final WailaRegistrar INSTANCE = new WailaRegistrar();

    private final Map<Class, List<IWailaDataProvider>> blockStackProviders;
    private final EnumMap<TooltipPosition, Map<Class, List<IWailaDataProvider>>> blockComponentProviders;
    private final Map<Class, List<IServerDataProvider<BlockEntity>>> blockDataProviders;

    private final Map<Class, List<IWailaEntityProvider>> entityOverrideProviders;
    private final EnumMap<TooltipPosition, Map<Class, List<IWailaEntityProvider>>> entityComponentProviders;
    private final Map<Class, List<IServerDataProvider<LivingEntity>>> entityDataProviders;

    private final Map<Class, List<IWailaBlockDecorator>> blockDecorators;
    private final Map<Identifier, IWailaTooltipRenderer> tooltipRenderers;

    WailaRegistrar() {
        blockStackProviders = Maps.newLinkedHashMap();
        blockComponentProviders = new EnumMap<>(TooltipPosition.class);
        blockDataProviders = Maps.newLinkedHashMap();

        entityOverrideProviders = Maps.newLinkedHashMap();
        entityComponentProviders = new EnumMap<>(TooltipPosition.class);
        entityDataProviders = Maps.newLinkedHashMap();

        blockDecorators = Maps.newLinkedHashMap();
        tooltipRenderers = Maps.newLinkedHashMap();

        for (TooltipPosition position : TooltipPosition.values()) {
            blockComponentProviders.put(position, new LinkedHashMap<>());
            entityComponentProviders.put(position, new LinkedHashMap<>());
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
    public void registerStackProvider(IWailaDataProvider dataProvider, Class block) {
        registerProvider(dataProvider, block, blockStackProviders);
    }

    @Override
    public void registerComponentProvider(IWailaDataProvider dataProvider, TooltipPosition position, Class block) {
        registerProvider(dataProvider, block, blockComponentProviders.get(position));
    }

    @Override
    public void registerBlockDataProvider(IServerDataProvider<BlockEntity> dataProvider, Class block) {
        registerProvider(dataProvider, block, blockDataProviders);
    }

    @Override
    public void registerOverrideEntityProvider(IWailaEntityProvider dataProvider, Class entity) {
        registerProvider(dataProvider, entity, entityOverrideProviders);
    }

    @Override
    public void registerComponentProvider(IWailaEntityProvider dataProvider, TooltipPosition position, Class entity) {
        registerProvider(dataProvider, entity, entityComponentProviders.get(position));
    }

    public void registerEntityDataProvider(IServerDataProvider<LivingEntity> dataProvider, Class entity) {
        registerProvider(dataProvider, entity, entityDataProviders);
    }

    @Override
    public void registerDecorator(IWailaBlockDecorator decorator, Class block) {
        List<IWailaBlockDecorator> decorators = blockDecorators.computeIfAbsent(block, b -> Lists.newArrayList());
        decorators.add(decorator);
    }

    @Override
    public void registerTooltipRenderer(Identifier id, IWailaTooltipRenderer renderer) {
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

    public Map<Integer, List<IWailaDataProvider>> getHeadProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.HEAD));
    }

    public Map<Integer, List<IWailaDataProvider>> getBodyProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.BODY));
    }

    public Map<Integer, List<IWailaDataProvider>> getTailProviders(Object block) {
        return getProviders(block, blockComponentProviders.get(TooltipPosition.TAIL));
    }

    public Map<Integer, List<IWailaDataProvider>> getStackProviders(Object block) {
        return getProviders(block, blockStackProviders);
    }

    public Map<Integer, List<IServerDataProvider<BlockEntity>>> getNBTProviders(Object block) {
        return getProviders(block, blockDataProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getHeadEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.HEAD));
    }

    public Map<Integer, List<IWailaEntityProvider>> getBodyEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.BODY));
    }

    public Map<Integer, List<IWailaEntityProvider>> getTailEntityProviders(Object entity) {
        return getProviders(entity, entityComponentProviders.get(TooltipPosition.TAIL));
    }

    public Map<Integer, List<IWailaEntityProvider>> getOverrideEntityProviders(Object entity) {
        return getProviders(entity, entityOverrideProviders);
    }

    public Map<Integer, List<IServerDataProvider<LivingEntity>>> getNBTEntityProviders(Object entity) {
        return getProviders(entity, entityDataProviders);
    }

    public Map<Integer, List<IWailaBlockDecorator>> getBlockDecorators(Object block) {
        return getProviders(block, blockDecorators);
    }

    public IWailaTooltipRenderer getTooltipRenderer(Identifier id) {
        return this.tooltipRenderers.get(id);
    }

    private  <T> Map<Integer, List<T>> getProviders(Object obj, Map<Class, List<T>> target) {
        Map<Integer, List<T>> returnList = new TreeMap<>();
        Integer index = 0;

        for (Class clazz : target.keySet()) {
            if (clazz.isInstance(obj))
                returnList.put(index, target.get(clazz));

            index++;
        }

        return returnList;
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
