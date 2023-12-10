package mcp.mobius.waila.plugin.core.pick;

import java.util.Objects;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPickerAccessor;
import mcp.mobius.waila.api.IPickerResults;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
public enum ObjectPicker implements IObjectPicker {

    INSTANCE;

    @Override
    public void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config) {
        var camera = accessor.getCameraEntity();

        if (camera == null) {
            return;
        }

        var showBlock = config.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK);
        var showFluid = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
        var showEntity = config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);

        if (!(showBlock || showFluid || showEntity)) {
            return;
        }

        var frameDelta = accessor.getFrameDelta();
        var maxDistance = accessor.getMaxDistance();

        var viewVec = camera.getViewVector(frameDelta);
        var start = camera.getEyePosition(frameDelta);
        var end = start.add(viewVec.x * maxDistance, viewVec.y * maxDistance, viewVec.z * maxDistance);

        var world = camera.level();

        if (showBlock || showFluid) {
            BlockGetter.traverseBlocks(start, end, Unit.INSTANCE, (unit, pos) -> {
                if (showBlock) {
                    var blockState = world.getBlockState(pos);

                    if (!blockState.isAir()) {
                        var blockShape = blockState.getShape(world, pos);
                        var blockHit = world.clipWithInteractionOverride(start, end, pos.immutable(), blockShape, blockState);

                        if (blockHit != null) {
                            results.add(blockHit, start.distanceToSqr(blockHit.getLocation()));
                        }
                    }
                }

                if (showFluid) {
                    var fluidState = world.getFluidState(pos);

                    if (fluidState.isSource()) {
                        var fluidShape = fluidState.getShape(world, pos);
                        var fluidHit = fluidShape.clip(start, end, pos);

                        if (fluidHit != null) {
                            results.add(fluidHit, start.distanceToSqr(fluidHit.getLocation()));
                        }
                    }
                }

                return null;
            }, unit -> unit);
        }

        if (showEntity) {
            var entities = camera.level().getEntities(camera, new AABB(start, end), EntitySelector.ENTITY_STILL_ALIVE);

            for (var entity : entities) {
                var bounds = entity.getBoundingBox();
                var clip = bounds.clip(start, end).orElse(null);

                if (bounds.contains(start)) {
                    clip = Objects.requireNonNullElse(clip, start);
                }

                if (clip != null) {
                    results.add(new EntityHitResult(entity, clip), start.distanceToSqr(clip));
                }
            }
        }
    }

}
