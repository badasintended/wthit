package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Component that will grow in size relative to overall tooltip width.
 * <p>
 * The size in which the component will grow is calculated by dividing
 * the available space with how much {@link GrowingComponent}s are in a line.
 */
@ApiSide.ClientOnly
public enum GrowingComponent implements ITooltipComponent.HorizontalGrowing {

    INSTANCE;

    @Override
    public int getMinimalWidth() {
        return 0;
    }

    @Override
    public void setGrownWidth(int grownWidth) {
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
    }

}
