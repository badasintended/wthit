package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IObjectPicker {

    HitResult MISS = new HitResult(Vec3.ZERO) {
        @Override
        public @NotNull Type getType() {
            return Type.MISS;
        }
    };

    /**
     * Returns the object that Waila will show the tooltip to,
     * typically by raycasting from the player's eye.
     *
     * @param client      the client instance
     * @param maxDistance the maximum reach distance
     * @param frameDelta  the frame delta
     * @param config      the plugin config
     *
     * @see #MISS
     * @see WailaConstants#CONFIG_SHOW_BLOCK
     * @see WailaConstants#CONFIG_SHOW_ENTITY
     * @see WailaConstants#CONFIG_SHOW_FLUID
     */
    HitResult pick(Minecraft client, double maxDistance, float frameDelta, IPluginConfig config);

}
