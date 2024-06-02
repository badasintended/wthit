package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Component that "renders" an empty space.
 */
@ApiSide.ClientOnly
public class SpacingComponent implements ITooltipComponent {

    public SpacingComponent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private final int width, height;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
    }

}
