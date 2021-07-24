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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RaycastUtil {

    private static final HitResult MISS = new HitResult(Vec3.ZERO) {
        @Override
        public Type getType() {
            return Type.MISS;
        }
    };

    public static HitResult fire() {
        Minecraft client = Minecraft.getInstance();
        Entity camera = client.getCameraEntity();

        return camera != null
            ? fire(camera, client.gameMode.getPickRange(), client.getFrameTime())
            : MISS;
    }

    public static HitResult fire(Entity camera, double playerReach, float tickDelta) {
        Level world = camera.level;
        Vec3 eyePosition = camera.getEyePosition(tickDelta);
        Vec3 lookVector = camera.getViewVector(tickDelta);
        Vec3 traceEnd = eyePosition.add(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        if (PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_ENTITY)) {
            EntityHitResult result = ProjectileUtil.getEntityHitResult(world, camera, eyePosition, traceEnd, new AABB(eyePosition, traceEnd), EntitySelector.ENTITY_STILL_ALIVE);
            if (result != null) {
                return result;
            }
        }

        Fluid fluidView = PluginConfig.INSTANCE.get(WailaConstants.CONFIG_SHOW_FLUID)
            ? Fluid.SOURCE_ONLY
            : Fluid.NONE;

        ClipContext context = new ClipContext(eyePosition, traceEnd, Block.OUTLINE, fluidView, camera);
        return world.clip(context);
    }

}
