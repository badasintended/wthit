package mcp.mobius.waila.plugin.core.raycast;

import mcp.mobius.waila.api.IRayCastVectorProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("DataFlowIssue")
public enum CoreRayCastVectorProvider implements IRayCastVectorProvider {

    INSTANCE;

    @Override
    public Vec3 getOrigin(float delta) {
        return Minecraft.getInstance().getCameraEntity().getEyePosition(delta);
    }

    @Override
    public Vec3 getDirection(float delta) {
        return Minecraft.getInstance().getCameraEntity().getViewVector(delta);
    }

}
