package mcp.mobius.waila.plugin.core.pick;

import mcp.mobius.waila.api.IObjectPicker;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public enum ObjectPicker implements IObjectPicker {

    INSTANCE;

    @Override
    public HitResult pick(Minecraft client, double maxDistance, float frameDelta, IPluginConfig config) {
        Entity camera = client.getCameraEntity();
        if (camera == null) {
            return MISS;
        }

        Vec3 viewVec = camera.getViewVector(frameDelta);
        Vec3 start = camera.getEyePosition(frameDelta);
        Vec3 end = start.add(viewVec.x * maxDistance, viewVec.y * maxDistance, viewVec.z * maxDistance);

        ClipContext.Fluid fluidContext = config.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE;
        BlockHitResult blockHit = camera.level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, fluidContext, camera));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) {
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(camera, start, end, new AABB(start, end), EntitySelector.ENTITY_STILL_ALIVE, 0f);

            if (entityHit != null) {
                if (blockHit.getType() == HitResult.Type.MISS) {
                    return entityHit;
                }

                double blockDistance = blockHit.getLocation().distanceToSqr(start);
                double entityDistance = entityHit.getLocation().distanceToSqr(start);

                if (entityDistance < blockDistance) {
                    return entityHit;
                }
            }
        }

        return blockHit;
    }

}
