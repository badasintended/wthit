package mcp.mobius.waila.util;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class RaycastUtil {

    private static final HitResult MISS = new HitResult(Vec3d.ZERO) {
        @Override
        public Type getType() {
            return Type.MISS;
        }
    };

    public static HitResult fire() {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity camera = client.getCameraEntity();

        return camera != null
            ? fire(camera, client.interactionManager.getReachDistance(), client.getTickDelta())
            : MISS;
    }

    public static HitResult fire(Entity camera, double playerReach, float tickDelta) {
        World world = camera.world;
        Vec3d eyePosition = camera.getCameraPosVec(tickDelta);
        Vec3d lookVector = camera.getRotationVec(tickDelta);
        Vec3d traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        if (PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ENTITY)) {
            EntityHitResult result = ProjectileUtil.getEntityCollision(world, camera, eyePosition, traceEnd, new Box(eyePosition, traceEnd), EntityPredicates.VALID_ENTITY);
            if (result != null) {
                return result;
            }
        }

        FluidHandling fluidView = PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_FLUID)
            ? FluidHandling.SOURCE_ONLY
            : FluidHandling.NONE;

        RaycastContext context = new RaycastContext(eyePosition, traceEnd, ShapeType.OUTLINE, fluidView, camera);
        return world.raycast(context);
    }

}
