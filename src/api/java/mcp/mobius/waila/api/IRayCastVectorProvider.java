package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Decides where Waila will start ray casting for objects.
 *
 * @see IClientRegistrar#rayCastVector(IRayCastVectorProvider, int)
 */
@ApiSide.ClientOnly
@ApiStatus.Experimental
public interface IRayCastVectorProvider {

    /**
     * Returns whether this provider instance should be used.
     *
     * @param config current plugin configurations
     */
    default boolean isEnabled(IPluginConfig config) {
        return true;
    }

    /**
     * Returns the ray cast origin.
     *
     * @see Entity#getEyePosition(float)
     */
    Vec3 getOrigin(float delta);

    /**
     * Returns the ray cast direction.
     *
     * @see Entity#getViewVector(float)
     */
    Vec3 getDirection(float delta);

}
