package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated use {@link IRayCastVectorProvider}
 */
// TODO: Remove
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IObjectPicker {

    default void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config) {
        results.add(pick(accessor.getClient(), accessor.getMaxDistance(), accessor.getFrameDelta(), config), 0);
    }

    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    HitResult MISS = new HitResult(Vec3.ZERO) {
        @Override
        public @NotNull Type getType() {
            return Type.MISS;
        }
    };

    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.20")
    @SuppressWarnings({"DeprecatedIsStillUsed", "unused"})
    default HitResult pick(Minecraft client, double maxDistance, float frameDelta, IPluginConfig config) {
        pick(IApiService.INSTANCE.getPickerAccessor(), IApiService.INSTANCE.getPickerResults(), config);

        for (var pickerResult : IApiService.INSTANCE.getPickerResults()) {
            return pickerResult;
        }

        return MISS;
    }

}
