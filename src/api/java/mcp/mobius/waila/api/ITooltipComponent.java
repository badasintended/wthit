package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

/**
 * See {@link mcp.mobius.waila.api.component} for default implementations.
 */
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface ITooltipComponent {

    int getWidth();

    int getHeight();

    void render(GuiGraphics ctx, int x, int y, float delta);

}
