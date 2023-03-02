package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IPickerAccessor {

    /**
     * @return the camera entity, this is tipically where to cast from
     */
    @Nullable
    Entity getCameraEntity();

    /**
     * @return the maximum distance the casting should be performed
     */
    double getMaxDistance();

    float getFrameDelta();

    Minecraft getClient();

}
