package mcp.mobius.waila.util;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

public final class RaycastUtil {

    private static final HitResult MISS = new HitResult(Vec3.ZERO) {
        @Override
        public Type getType() {
            return Type.MISS;
        }
    };

    @SuppressWarnings("ConstantConditions")
    public static HitResult fire() {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();

        return camera != null
            ? fire(camera, client.gameMode.getPickRange(), client.getFrameTime())
            : MISS;
    }

    public static HitResult fire(Entity camera, double reach, float tickDelta) {
        Vec3 viewVec = camera.getViewVector(tickDelta);

        Vec3 start = camera.getEyePosition(tickDelta);
        Vec3 end = start.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);

        Fluid fluidContext = PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_FLUID) ? Fluid.SOURCE_ONLY : Fluid.NONE;
        BlockHitResult blockHit = camera.level.clip(new ClipContext(start, end, Block.OUTLINE, fluidContext, camera));

        if (PluginConfig.INSTANCE.getBoolean(WailaConstants.CONFIG_SHOW_ENTITY)) {
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(camera, start, end, new AABB(start, end), EntitySelector.ENTITY_STILL_ALIVE, 0f);

            if (entityHit != null) {
                if (blockHit.getType() == Type.MISS) {
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
