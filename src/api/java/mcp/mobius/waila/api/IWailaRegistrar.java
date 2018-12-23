package mcp.mobius.waila.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface IWailaRegistrar {

    /**
     * Registers a namespaced config key to be accessed within data providers.
     *
     * @param key the namespaced key
     * @param defaultValue the default value
     */
    void addConfig(Identifier key, boolean defaultValue);

    /**
     * Registers a namespaced config key to be accessed within data providers. These values are sent from the server to
     * the client upon connection.
     *
     * @param key The namespaced key
     * @param defaultValue The default value
     */
    void addSyncedConfig(Identifier key, boolean defaultValue);

    /**
     * Registers an {@link IWailaDataProvider} instance to allow overriding the displayed item for a block via the
     * {@link IWailaDataProvider#getStack(IWailaDataAccessor, IWailaConfigHandler)} method. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param dataProvider The data provider instance
     * @param block The highest level class to apply to
     */
    void registerStackProvider(IWailaDataProvider dataProvider, Class block);

    /**
     * Registers an {@link IWailaDataProvider} instance for appending {@link net.minecraft.text.TextComponent} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param dataProvider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param block The highest level class to apply to
     */
    void registerComponentProvider(IWailaDataProvider dataProvider, TooltipPosition position, Class block);

    /**
     * Registers an {@link IServerDataProvider<BlockEntity>} instance for data syncing purposes. A {@link BlockEntity}
     * is also an acceptable class type.
     *
     * @param dataProvider The data provider instance
     * @param block The highest level class to apply to
     */
    void registerBlockDataProvider(IServerDataProvider<BlockEntity> dataProvider, Class block);

    /**
     * Registers an {@link IWailaEntityProvider} instance to allow overriding the entity being displayed.
     *
     * @param dataProvider The data provider instance
     * @param entity The highest level class to apply to
     */
    void registerOverrideEntityProvider(IWailaEntityProvider dataProvider, Class entity);

    /**
     * Registers an {@link IWailaEntityProvider} instance for appending {@link net.minecraft.text.TextComponent} to the tooltip.
     *
     * @param dataProvider The data provider instance
     * @param position The position on the tooltip this applies to
     * @param entity The highest level class to apply to
     */
    void registerComponentProvider(IWailaEntityProvider dataProvider, TooltipPosition position, Class entity);

    /**
     * Registers an {@link IWailaEntityProvider} instance for data syncing purposes.
     *
     * @param dataProvider The data provider instance
     * @param entity The highest level class to apply to
     */
    void registerEntityDataProvider(IServerDataProvider<LivingEntity> dataProvider, Class entity);

    /**
     * Registers an {@link IWailaBlockDecorator} instance to allow rendering content in the world while looking at the block.
     *
     * @param decorator The decorator instance
     * @param block The highest level class to apply to
     */
    void registerDecorator(IWailaBlockDecorator decorator, Class block);

    /**
     * Registers an {@link IWailaTooltipRenderer} to allow passing a data string as a component to be rendered as a graphic
     * instead.
     *
     * @param id The identifier for lookup
     * @param renderer The renderer instance
     */
    void registerTooltipRenderer(Identifier id, IWailaTooltipRenderer renderer);
}
