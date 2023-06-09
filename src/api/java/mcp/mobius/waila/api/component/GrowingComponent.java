package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A component that will grow in size relative to overall tooltip width.
 * <p>
 * The size in which the component will grow is calculated by dividing
 * the available space with how much {@link GrowingComponent}s are in a line.
 */
@ApiSide.ClientOnly
public enum GrowingComponent implements ITooltipComponent {

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
    public void render(GuiGraphics ctx, int x, int y, float delta) {
    }

}
