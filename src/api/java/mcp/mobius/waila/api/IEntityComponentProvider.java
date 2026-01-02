package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used to provide {@link Entity} tooltip information to Waila.
 * <p>
 * All methods in this interface <b>shouldn't</b> to be called by the implementing mod.
 * An instance of the class is to be registered via the {@link IClientRegistrar} instance provided in {@link IWailaClientPlugin}.
 *
 * @see IWailaClientPlugin
 * @see IClientRegistrar
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IEntityComponentProvider {

    /**
     * An "empty" entity to be used with {@link #getOverride}, effectively disabling the tooltip to be displayed.
     * <p>
     * <b>Note:</b> Waila will use {@code ==} instead of {@link Object#equals} to check for this.
     */
    Entity EMPTY_ENTITY = Internals.unsafeAlloc(AreaEffectCloud.class);

    /**
     * Redirect the ray cast hit result to target other object.
     *
     * @param redirect the redirector
     * @param accessor contains most of the relevant information about the current environment.
     *                 Note that {@link IEntityAccessor#getData()} will always be empty at this time
     * @param config   current plugin configuration
     *
     * @return {@code null} if this method doesn't redirect to anything,
     * any result from one of {@link ITargetRedirector}'s methods otherwise
     *
     * @see IClientRegistrar#redirect(IEntityComponentProvider, Class)
     */
    @Nullable
    @ApiStatus.Experimental
    default ITargetRedirector.Result redirect(ITargetRedirector redirect, IEntityAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to send additional context to {@link IDataProvider}s.
     *
     * @param ctx      the context writer
     * @param accessor contains most of the relevant information about the current environment.
     *                 Note that {@link IEntityAccessor#getData()} will always be empty at this time
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#dataContext(IEntityComponentProvider, Class)
     */
    default void appendDataContext(IDataWriter ctx, IEntityAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to override the default Waila lookup system.
     *
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @return {@code null} if override is not required, an {@link Entity} otherwise
     *
     * @see IClientRegistrar#override(IEntityComponentProvider, Class, int)
     * @see #EMPTY_ENTITY
     */
    @Nullable
    default Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to set an icon to display in the tooltip.
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IEntityAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @return the component to render or {@code null} if this provider doesn't decide it
     *
     * @see IClientRegistrar#icon(IEntityComponentProvider, Class, int)
     */
    @Nullable
    default ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IEntityAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#head(IEntityComponentProvider, Class, int)
     */
    default void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IEntityAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#body(IEntityComponentProvider, Class, int)
     */
    default void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IEntityAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#tail(IEntityComponentProvider, Class, int)
     */
    default void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
    }

}
