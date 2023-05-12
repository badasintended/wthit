package mcp.mobius.waila.plugin.core.pick;

import java.util.List;
import java.util.Objects;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPickerAccessor;
import mcp.mobius.waila.api.IPickerResults;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum ObjectPicker implements IObjectPicker {

    INSTANCE;

    @Override
    public void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config) {
        Entity camera = accessor.getCameraEntity();

        if (camera == null) {
            return;
        }

        boolean showBlock = config.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK);
        boolean showFluid = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
        boolean showEntity = config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);

        if (!(showBlock || showFluid || showEntity)) {
            return;
        }

        float frameDelta = accessor.getFrameDelta();
        double maxDistance = accessor.getMaxDistance();

        Vec3 viewVec = camera.getViewVector(frameDelta);
        Vec3 start = camera.getEyePosition(frameDelta);
        Vec3 end = start.add(viewVec.x * maxDistance, viewVec.y * maxDistance, viewVec.z * maxDistance);

        Level world = camera.level();

        if (showBlock || showFluid) {
            BlockGetter.traverseBlocks(start, end, Unit.INSTANCE, (unit, pos) -> {
                if (showBlock) {
                    BlockState blockState = world.getBlockState(pos);

                    if (!blockState.isAir()) {
                        VoxelShape blockShape = blockState.getShape(world, pos);
                        BlockHitResult blockHit = world.clipWithInteractionOverride(start, end, pos.immutable(), blockShape, blockState);

                        if (blockHit != null) {
                            results.add(blockHit, start.distanceToSqr(blockHit.getLocation()));
                        }
                    }
                }

                if (showFluid) {
                    FluidState fluidState = world.getFluidState(pos);

                    if (fluidState.isSource()) {
                        VoxelShape fluidShape = fluidState.getShape(world, pos);
                        BlockHitResult fluidHit = fluidShape.clip(start, end, pos);

                        if (fluidHit != null) {
                            results.add(fluidHit, start.distanceToSqr(fluidHit.getLocation()));
                        }
                    }
                }

                return null;
            }, unit -> unit);
        }

        if (showEntity) {
            List<Entity> entities = camera.level().getEntities(camera, new AABB(start, end), EntitySelector.ENTITY_STILL_ALIVE);

            for (Entity entity : entities) {
                AABB bounds = entity.getBoundingBox();
                Vec3 clip = bounds.clip(start, end).orElse(null);

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
