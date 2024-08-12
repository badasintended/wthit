package mcp.mobius.waila.api;

import lol.bai.badpackets.impl.marker.ApiSide;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IClientRegistrar {

    /**
     * Adds an event listener
     */
    void addEventListener(IEventListener listener, int priority);

    /**
     * Adds an event listener
     */
    void addEventListener(IEventListener listener);

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
    <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz, int priority);

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
    <T> void addRedirect(IBlockComponentProvider provider, Class<T> clazz);

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
    <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz, int priority);

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
    <T> void addOverride(IBlockComponentProvider provider, Class<T> clazz);

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
    <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow overriding the displayed icon for a block via the
     * {@link IBlockComponentProvider#getIcon(IBlockAccessor, IPluginConfig)} method.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void addIcon(IBlockComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IBlockComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for appending {@link Component} to the tooltip.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     */
    <T> void addComponent(IBlockComponentProvider provider, TooltipPosition position, Class<T> clazz);

    /**
     * Registers an {@link IBlockComponentProvider} instance for appending data context that get sent to the server.
     * A {@link BlockEntity} is also an acceptable class type.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void addDataContext(IBlockComponentProvider provider, Class<T> clazz);

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
    <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow redirecting the object being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    @ApiStatus.Experimental
    <T> void addRedirect(IEntityComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IEntityComponentProvider} instance to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow overriding the entity being displayed.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void addOverride(IEntityComponentProvider provider, Class<T> clazz);

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
    <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} to allow displaying an icon via the
     * {@link IEntityComponentProvider#getIcon(IEntityAccessor, IPluginConfig)} method.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void addIcon(IEntityComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     *
     * @see WailaConstants#DEFAULT_PRIORITY
     */
    <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz, int priority);

    /**
     * Registers an {@link IEntityComponentProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority} for appending {@link Component} to the tooltip.
     *
     * @param provider the data provider instance
     * @param position the position on the tooltip this applies to
     * @param clazz    the highest level class to apply to
     */
    <T> void addComponent(IEntityComponentProvider provider, TooltipPosition position, Class<T> clazz);

    /**
     * Registers an {@link IEntityComponentProvider} instance for appending data context that get sent to the server.
     *
     * @param provider the data provider instance
     * @param clazz    the highest level class to apply to
     */
    <T> void addDataContext(IEntityComponentProvider provider, Class<T> clazz);

    /**
     * Registers an {@link IThemeType} instance.
     *
     * @param id   the theme type id
     * @param type the theme type
     */
    @ApiStatus.Experimental
    <T extends ITheme> void addThemeType(ResourceLocation id, IThemeType<T> type);

    /**
     * Registers an {@link IRayCastVectorProvider} instance
     *
     * @param provider the vector provider
     * @param priority the priority of this provider <b>0 is the minimum, lower number will be called first</b>
     */
    @ApiStatus.Experimental
    void addRayCastVector(IRayCastVectorProvider provider, int priority);

    /**
     * Registers an {@link IRayCastVectorProvider} instance with
     * {@linkplain WailaConstants#DEFAULT_PRIORITY default priority}
     *
     * @param provider the vector provider
     */
    @ApiStatus.Experimental
    void addRayCastVector(IRayCastVectorProvider provider);

    /**
     * Registers a tool type, to be used for the harvestability tooltip.
     *
     * @param id       the tool type id, also used as the translation key as {@code tooltip.waila.harvest.tool.[namespace].[path]}
     * @param toolType the tool type
     */
    @ApiStatus.Experimental
    void addToolType(ResourceLocation id, IToolType toolType);

}
