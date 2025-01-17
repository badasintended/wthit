package mcp.mobius.waila.gui.hud;

import java.util.Objects;
import java.util.function.ObjDoubleConsumer;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayCaster {

    public static void cast(Level world, Entity camera, Vec3 origin, Vec3 direction, double maxDistance, ObjDoubleConsumer<HitResult> results) {
        var showBlock = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_BLOCK);
        var showFluid = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID);
        var showEntity = PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY);

        var max = origin.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);

        if (showBlock || showFluid) {
            BlockGetter.traverseBlocks(origin, max, Unit.INSTANCE, (unit, pos) -> {
                if (showBlock) {
                    var blockState = world.getBlockState(pos);

                    if (!blockState.isAir()) {
                        var blockShape = blockState.getShape(world, pos);
                        var blockHit = world.clipWithInteractionOverride(origin, max, pos.immutable(), blockShape, blockState);

                        if (blockHit != null) {
                            results.accept(blockHit, origin.distanceToSqr(blockHit.getLocation()));
                        }
                    }
                }

                if (showFluid) {
                    var fluidState = world.getFluidState(pos);

                    if (fluidState.isSource()) {
                        var fluidShape = fluidState.getShape(world, pos);
                        var fluidHit = fluidShape.clip(origin, max, pos.immutable());

                        if (fluidHit != null) {
                            results.accept(fluidHit, origin.distanceToSqr(fluidHit.getLocation()));
                        }
                    }
                }

                return null;
            }, unit -> unit);
        }

        if (showEntity) {
            var entities = world.getEntities(camera, new AABB(origin, max), EntitySelector.ENTITY_STILL_ALIVE);

            for (var entity : entities) {
                var bounds = entity.getBoundingBox();
                var clip = bounds.clip(origin, max).orElse(null);

                if (bounds.contains(origin)) {
                    clip = Objects.requireNonNullElse(clip, origin);
                }

                if (clip != null) {
                    results.accept(new EntityHitResult(entity, clip), origin.distanceToSqr(clip));
                }
            }
        }
    }

}
