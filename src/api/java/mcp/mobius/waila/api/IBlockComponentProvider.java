package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used to provide {@link Block}/{@link BlockEntity} tooltip information to Waila.
 * <p>
 * All methods in this interface <b>shouldn't</b> to be called by the implementing mod.
 * An instance of the class is to be registered via the {@link IClientRegistrar} instance provided in {@link IWailaClientPlugin}.
 *
 * @see IWailaClientPlugin
 * @see IClientRegistrar
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IBlockComponentProvider {

    /**
     * An "empty" block state to be used with {@link #getOverride}, effectively disabling the tooltip to be displayed.
     * <p>
     * <b>Note:</b> Waila will use {@code ==} instead of {@link Object#equals} to check for this.
     */
    BlockState EMPTY_BLOCK_STATE = Internals.unsafeAlloc(BlockState.class);

    /**
     * Redirect the ray cast hit result to target other object.
     *
     * @param redirect the redirector
     * @param accessor contains most of the relevant information about the current environment.
     *                 Note that {@link IBlockAccessor#getData()} will always be empty at this time
     * @param config   current plugin configuration
     *
     * @return {@code null} if this method doesn't redirect to anything,
     * any result from one of {@link ITargetRedirector}'s methods otherwise
     *
     * @see IClientRegistrar#redirect(IBlockComponentProvider, Class)
     */
    @Nullable
    @ApiStatus.Experimental
    default ITargetRedirector.Result redirect(ITargetRedirector redirect, IBlockAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to send additional context to {@link IDataProvider}s.
     *
     * @param ctx      the context writer
     * @param accessor contains most of the relevant information about the current environment.
     *                 Note that {@link IBlockAccessor#getData()} will always be empty at this time
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#dataContext(IBlockComponentProvider, Class)
     */
    default void appendDataContext(IDataWriter ctx, IBlockAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to override the default Waila lookup system.
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IBlockAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @return {@code null} if override is not required, a {@link BlockState} otherwise
     *
     * @see IClientRegistrar#override(IBlockComponentProvider, Class, int)
     * @see #EMPTY_BLOCK_STATE
     */
    @Nullable
    default BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to set an icon to display in the tooltip.
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IBlockAccessor#getData()}.
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
    default ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        return null;
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IBlockAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#head(IBlockComponentProvider, Class, int)
     */
    default void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IBlockAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#body(IBlockComponentProvider, Class, int)
     */
    default void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).
     * <p>
     * This method is only called on the client side.
     * If you require data from the server, you should also implement {@link IDataProvider}
     * and add the data there, which can then be read back using {@link IBlockAccessor#getData()}.
     * If you rely on the client knowing the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip  current list of tooltip lines (might have been processed by other providers and might be processed by other providers),
     *                 use {@link ITooltip#setLine} with tags from {@link WailaConstants} to override built-in values
     * @param accessor contains most of the relevant information about the current environment
     * @param config   current plugin configuration
     *
     * @see IClientRegistrar#tail(IBlockComponentProvider, Class, int)
     */
    default void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
    }

}
