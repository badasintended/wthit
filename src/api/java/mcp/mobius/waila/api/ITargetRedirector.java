package mcp.mobius.waila.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to redirect the targeted objet to other object.
 *
 * @see IBlockComponentProvider#redirect(ITargetRedirector, IBlockAccessor, IPluginConfig)
 * @see IEntityComponentProvider#redirect(ITargetRedirector, IEntityAccessor, IPluginConfig)
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ITargetRedirector {

    /**
     * Redirect the current object to itself.
     * <p>
     * This also restrict lower priority provider from redirecting the object,
     * return {@code null} instead if the caller doesn't redirect.
     */
    Result toSelf();

    /**
     * Redirect to nowhere, disabling the tooltip to be displayed.
     */
    Result toNowhere();

    /**
     * Redirect to whatever object behind this object, essentially making this object appear invisible.
     */
    Result toBehind();

    /**
     * Redirect to specific object at the hit result's position.
     * <p>
     * <b>Note:</b> {@link BlockHitResult#withPosition(BlockPos)} will have the original
     * {@linkplain HitResult#getLocation() hit location} (not to be confused with
     * {@linkplain BlockHitResult#getBlockPos() block position}), you need to take
     * account if the redirect target is not from something you own (e.g. redirecting
     * to other mod's block).
     */
    Result to(HitResult hitResult);

    /**
     * Redirection result, only here to make sure it's not called multiple times.
     */
    @ApiStatus.NonExtendable
    interface Result {

    }

}
