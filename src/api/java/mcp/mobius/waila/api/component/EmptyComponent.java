package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Component that renders nothing.
 */
@ApiSide.ClientOnly
public enum EmptyComponent implements ITooltipComponent {

    INSTANCE;

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
    }

}
