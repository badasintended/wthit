package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IObjectPicker {

    /**
     * Returns the object that Waila will show the tooltip to, typically by raycasting from the camera's eye.
     * <p>
     * <b>Note:</b> {@link IPickerResults#add return} all objects in a line from cast origin up until
     * {@link IPickerAccessor#getMaxDistance() max distance}, that way Waila can try to show tooltip
     * for further object if the tooltip for nearer object is disabled for some reason.
     * <p>
     * Check for config value to save some processing time.
     *
     * @see WailaConstants#CONFIG_SHOW_BLOCK
     * @see WailaConstants#CONFIG_SHOW_ENTITY
     * @see WailaConstants#CONFIG_SHOW_FLUID
     */
    default void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config) {
        results.add(pick(accessor.getClient(), accessor.getMaxDistance(), accessor.getFrameDelta(), config), 0);
    }

    /**
     * @deprecated not needed on newest API.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    HitResult MISS = new HitResult(Vec3.ZERO) {
        @Override
        public @NotNull Type getType() {
            return Type.MISS;
        }
    };

    /**
     * @deprecated override {@link #pick(IPickerAccessor, IPickerResults, IPluginConfig)} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    @SuppressWarnings({"DeprecatedIsStillUsed", "unused"})
    default HitResult pick(Minecraft client, double maxDistance, float frameDelta, IPluginConfig config) {
        pick(IApiService.INSTANCE.getPickerAccessor(), IApiService.INSTANCE.getPickerResults(), config);

        for (HitResult pickerResult : IApiService.INSTANCE.getPickerResults()) {
            return pickerResult;
        }

        return MISS;
    }

}
