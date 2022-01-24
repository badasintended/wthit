package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;

/**
 * A tooltip component that "renders" an empty space.
 */
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
    public void render(PoseStack matrices, int x, int y, float delta) {
    }

}
