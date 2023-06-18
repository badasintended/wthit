package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiComponent;

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
    public void render(PoseStack matrices, int x, int y, float delta) {
        GuiComponent.fill(matrices, x, y, x + width, y + height, argb);
    }

}
