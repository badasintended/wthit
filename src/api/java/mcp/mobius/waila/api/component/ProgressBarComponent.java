package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Component that renders a horizontal progress bar.
 */
public class ProgressBarComponent implements ITooltipComponent.HorizontalGrowing {

    /**
     * @param height     the height of the bar
     * @param foreground the color of current progress
     * @param background the background color
     */
    public ProgressBarComponent(int height, int foreground, int background, float progress) {
        this.height = height;
        this.foreground = foreground;
        this.background = background;
        this.progress = progress;
    }

    private int width;
    private final int height;
    private final int foreground;
    private final int background;
    private final float progress;

    @Override
    public int getMinimalWidth() {
        return 50;
    }

    @Override
    public void setGrownWidth(int grownWidth) {
        this.width = grownWidth;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var x1 = x;
        var x2 = x + ((int) (width * progress));
        var y2 = y + height;
        ctx.fill(x1, y, x2, y2, foreground);

        x1 = x2;
        x2 = x + width;
        ctx.fill(x1, y, x2, y2, background);
    }

}
