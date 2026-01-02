package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

/**
 * The registrar for client-sided objects.
 */
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IClientRegistrar {

    /**
     * Adds an event listener
     */
    void eventListener(IEventListener listener, int priority);

    /**
     * Adds an event listener
     */
    default void eventListener(IEventListener listener) {
        eventListener(listener, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow redirecting the object being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    @ApiStatus.Experimental
    <T> void redirect(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow redirecting the object being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    @ApiStatus.Experimental
    default <T> void redirect(IBlockComponentProvider provider, Class<T> clazz) {
        redirect(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void override(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow overriding the block being displayed.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    default <T> void override(IBlockComponentProvider provider, Class<T> clazz) {
        override(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance to allow overriding the displayed icon for a block via the
     * {@link IBlockComponentProvider#getIcon(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void icon(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow overriding the displayed icon for a block via the
     * {@link IBlockComponentProvider#getIcon(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T> void icon(IBlockComponentProvider provider, Class<T> clazz) {
        icon(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void head(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    default <T> void head(IBlockComponentProvider provider, Class<T> clazz) {
        head(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void body(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    default <T> void body(IBlockComponentProvider provider, Class<T> clazz) {
        body(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void tail(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    default <T> void tail(IBlockComponentProvider provider, Class<T> clazz) {
        tail(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending data context that get sent to the server.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void dataContext(IBlockComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow redirecting the object being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    @ApiStatus.Experimental
    <T> void redirect(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow redirecting the object being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    @ApiStatus.Experimental
    default <T> void redirect(IEntityComponentProvider provider, Class<T> clazz) {
        redirect(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void override(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    default <T> void override(IEntityComponentProvider provider, Class<T> clazz) {
        override(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow displaying an icon via the
     * {@link IEntityComponentProvider#getIcon(IEntityAccessor, IPluginConfig)} method.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void icon(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow displaying an icon via the
     * {@link IEntityComponentProvider#getIcon(IEntityAccessor, IPluginConfig)} method.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T> void icon(IEntityComponentProvider provider, Class<T> clazz) {
        icon(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void head(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T> void head(IEntityComponentProvider provider, Class<T> clazz) {
        head(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void body(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T> void body(IEntityComponentProvider provider, Class<T> clazz) {
        body(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void tail(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    default <T> void tail(IEntityComponentProvider provider, Class<T> clazz) {
        tail(provider, clazz, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending data context that get sent to the server.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void dataContext(IEntityComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IThemeType} instance.
     *
     * @param id   the theme type id
     * @param type the theme type
     */
    @ApiStatus.Experimental
    <T extends ITheme> void themeType(ResourceLocation id, IThemeType<T> type);

    /**
     * Registers an {@link IRayCastVectorProvider} instance
     *
     * @param provider the vector provider
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     */
    @ApiStatus.Experimental
    void rayCastVector(IRayCastVectorProvider provider, int priority);

    /**
     * Registers an {@link IRayCastVectorProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority}
     *
     * @param provider the vector provider
     */
    @ApiStatus.Experimental
    default void rayCastVector(IRayCastVectorProvider provider) {
        rayCastVector(provider, WailaConstants.DEFAULT_PRIORITY);
    }

    /**
     * Registers a tool type, to be used for the harvestability tooltip.
     *
     * @param id       the tool type id, also used as the translation key as {@code tooltip.waila.harvest.tool.[namespace].[path]}
     * @param toolType the tool type
     */
    @ApiStatus.Experimental
    void toolType(ResourceLocation id, IToolType toolType);

}
