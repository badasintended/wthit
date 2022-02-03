package mcp.mobius.waila.api;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.__internal__.ApiSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * See {@link mcp.mobius.waila.api.component} for default implementations.
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface ITooltipComponent {

    int getWidth();

    int getHeight();

    void render(PoseStack matrices, int x, int y, float delta);

}
