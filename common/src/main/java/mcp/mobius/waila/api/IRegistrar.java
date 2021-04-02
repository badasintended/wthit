package mcp.mobius.waila.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

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
     * Registers an {@link IComponentProvider} instance to allow overriding the displayed item for a block via the
     * {@link IComponentProvider#getStack(IDataAccessor, IPluginConfig)} method. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    <T> void registerStackProvider(int priority, IComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the displayed item for a block via the
     * {@link IComponentProvider#getStack(IDataAccessor, IPluginConfig)} method. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param clazz    The highest level class to apply to
     */
    default <T> void registerStackProvider(IComponentProvider provider, Class<T> clazz) {
        registerStackProvider(DEFAULT_PRIORITY, provider, clazz);
    }

    /**
     * Registers an {@link IComponentProvider} instance for appending {@link Text} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    <T> void registerComponentProvider(int priority, IComponentProvider provider, TooltipPosition position, Class<T> clazz);

    /**
     * Registers an {@link IComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Text} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param clazz    The highest level class to apply to
     */
    default <T> void registerComponentProvider(IComponentProvider provider, TooltipPosition position, Class<T> clazz) {
        registerComponentProvider(DEFAULT_PRIORITY, provider, position, clazz);
    }

    /**
     * Registers an {@link IServerDataProvider<BlockEntity>} instance for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param provider The data provider instance
     * @param block    The highest level class to apply to
     */
    <T> void registerBlockDataProvider(IServerDataProvider<BlockEntity> provider, Class<T> block);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow overriding the entity being displayed.
     *
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     * @param provider The data provider instance
     * @param entity   The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    <T> void registerOverrideEntityProvider(int priority, IEntityComponentProvider provider, Class<T> entity);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow overriding the entity being displayed.
     *
     * @param provider The data provider instance
     * @param entity   The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    default <T> void registerOverrideEntityProvider(IEntityComponentProvider provider, Class<T> entity) {
        registerOverrideEntityProvider(DEFAULT_PRIORITY, provider, entity);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow displaying an item next to the entity name.
     *
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     * @param provider The data provider instance
     * @param entity   The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    <T> void registerEntityStackProvider(int priority, IEntityComponentProvider provider, Class<T> entity);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} to allow displaying an item next to the entity name.
     *
     * @param provider The data provider instance
     * @param entity   The highest level class to apply to
     */
    default <T> void registerEntityStackProvider(IEntityComponentProvider provider, Class<T> entity) {
        registerEntityStackProvider(DEFAULT_PRIORITY, provider, entity);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Text} to the tooltip.
     *
     * @param priority The priority of this provider <b>0 is the minimum, lower number will be called first</b>
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param entity   The highest level class to apply to
     *
     * @see #DEFAULT_PRIORITY
     */
    <T> void registerComponentProvider(int priority, IEntityComponentProvider provider, TooltipPosition position, Class<T> entity);

    /**
     * Registers an {@link IEntityComponentProvider} instance with {@link #DEFAULT_PRIORITY} for appending {@link Text} to the tooltip.
     *
     * @param provider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param entity   The highest level class to apply to
     */
    default <T> void registerComponentProvider(IEntityComponentProvider provider, TooltipPosition position, Class<T> entity) {
        registerComponentProvider(DEFAULT_PRIORITY, provider, position, entity);
    }

    /**
     * Registers an {@link IServerDataProvider<Entity>} instance for data syncing purposes.
     *
     * @param provider The data provider instance
     * @param entity   The highest level class to apply to
     */
    <T> void registerEntityDataProvider(IServerDataProvider<Entity> provider, Class<T> entity);

    /**
     * Registers an {@link ITooltipRenderer} to allow passing a data string as a component to be rendered as a graphic
     * instead.
     *
     * @param id       The identifier for lookup
     * @param renderer The renderer instance
     */
    void registerTooltipRenderer(Identifier id, ITooltipRenderer renderer);

    /**
     * Registers an {@link IBlockDecorator} instance to allow rendering content in the world while looking at the block.
     * TODO: Remove in 1.17 release
     *
     * @param decorator The decorator instance
     * @param block     The highest level class to apply to
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    default <T> void registerDecorator(IBlockDecorator decorator, Class<T> block) {
    }

}
