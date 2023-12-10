package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated use {@link IRayCastVectorProvider}
 */
// TODO: Remove
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IPickerAccessor {

    @Nullable
    Entity getCameraEntity();

    double getMaxDistance();

    float getFrameDelta();

    Minecraft getClient();

}
