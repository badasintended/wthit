package mcp.mobius.waila.api;

import mcp.mobius.waila.api.internal.ApiSide;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IRegistrar {

    /**
     * The default priority for all component.
     */
    int DEFAULT_PRIORITY = 1000;

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key          the namespaced key
     * @param defaultValue the default value
     */
    @ApiSide.ClientOnly
    void addConfig(Identifier key, boolean defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers. These values are sent from the server to
     * the client upon connection.
     *
     * @param key          The namespaced key
     * @param defaultValue The default value
     */
    void addSyncedConfig(Identifier key, boolean defaultValue);

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    default <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the displayed item for a block via the
     * {@link IBlockComponentProvider#getDisplayItem(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addDisplayItem(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the displayed item for a block via the
     * {@link IBlockComponentProvider#getDisplayItem(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addDisplayItem(IBlockComponentProvider provider, Class<T> clazz) {
        addDisplayItem(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Text} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Text} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IServerDataProvider<BlockEntity>} instance for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ServerOnly
    <T> void addBlockData(IServerDataProvider<BlockEntity> provider, Class<T> clazz);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow overriding the entity being displayed.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the entity being displayed.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    default <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz) {
        addOverride(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow displaying an item next to the entity name.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addDisplayItem(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow displaying an item next to the entity name.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addDisplayItem(IEntityComponentProvider provider, Class<T> clazz) {
        addDisplayItem(provider, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Text} to the tooltip.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see #DEFAULT_PRIORITY
     */
    @ApiSide.ClientOnly
    <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Text} to the tooltip.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ClientOnly
    default <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz, DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IServerDataProvider<Entity>} instance for data syncing purposes.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     */
    @ApiSide.ServerOnly
    <T> void addEntityData(IServerDataProvider<Entity> provider, Class<T> clazz);

    /**
     * Registers an {@link ITooltipRenderer} to allow passing a data string as a component to be rendered as a graphic
     * instead.
     *
     * @param id       The identifier for lookup
     * @param renderer The renderer instance
     */
    @ApiSide.ClientOnly
    void addRenderer(Identifier id, ITooltipRenderer renderer);

    // TODO: Remove
    @Deprecated
    default <T> void registerDecorator(IBlockDecorator decorator, Class<T> block) {
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addDisplayItem(IBlockComponentProvider, Class)}
     */
    @Deprecated
    default <T> void registerStackProvider(IComponentProvider provider, Class<T> clazz) {
        addDisplayItem(provider, clazz);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addComponent(IBlockComponentProvider, TooltipPosition, Class)}
     */
    @Deprecated
    default <T> void registerComponentProvider(IComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addBlockData(IServerDataProvider, Class)}
     */
    @Deprecated
    default <T> void registerBlockDataProvider(IServerDataProvider<BlockEntity> provider, Class<T> clazz) {
        addBlockData(provider, clazz);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addOverride(IEntityComponentProvider, Class)}
     */
    @Deprecated
    default <T> void registerOverrideEntityProvider(IEntityComponentProvider provider, Class<T> entity) {
        addOverride(provider, entity);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addDisplayItem(IEntityComponentProvider, Class)}
     */
    @Deprecated
    default <T> void registerEntityStackProvider(IEntityComponentProvider provider, Class<T> entity) {
        addDisplayItem(provider, entity);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addComponent(IEntityComponentProvider, TooltipPosition, Class)}
     */
    @Deprecated
    default <T> void registerComponentProvider(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        addComponent(provider, position, clazz);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addEntityData(IServerDataProvider, Class)}
     */
    @Deprecated
    default <T> void registerEntityDataProvider(IServerDataProvider<Entity> provider, Class<T> clazz) {
        addEntityData(provider, clazz);
    }

    /**
     * TODO: Remove
     *
     * @deprecated use {@link #addRenderer(Identifier, ITooltipRenderer)}
     */
    @Deprecated
    default void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer) {
        addRenderer(id, renderer);
    }

}
