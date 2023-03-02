package mcp.mobius.waila.pick;

import mcp.mobius.waila.api.IPickerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public enum PickerAccessor implements IPickerAccessor {

    INSTANCE;

    private Minecraft client;
    private @Nullable Entity camera;
    private double maxDistance;
    private float frameDelta;

    @Override
    public @Nullable Entity getCameraEntity() {
        return camera;
    }

    @Override
    public Minecraft getClient() {
        return client;
    }

    @Override
    public double getMaxDistance() {
        return maxDistance;
    }

    @Override
    public float getFrameDelta() {
        return frameDelta;
    }

    public static PickerAccessor of(Minecraft client, @Nullable Entity camera, double maxDistance, float frameDelta) {
        INSTANCE.client = client;
        INSTANCE.camera = camera;
        INSTANCE.maxDistance = maxDistance;
        INSTANCE.frameDelta = frameDelta;
        return INSTANCE;
    }

}
