package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Component that renders a colored rectangle.
 */
@ApiSide.ClientOnly
public class ColorComponent implements ITooltipComponent {

    public ColorComponent(int width, int height, int argb) {
        this.width = width;
        this.height = height;
        this.argb = argb;
    }

    private final int width, height;
    private final int argb;

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
        ctx.fill(x, y, x + width, y + height, argb);
    }

}
