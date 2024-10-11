package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

/**
 * Component that renders a furnace-like progress arrow.
 */
@ApiSide.ClientOnly
public class ProgressArrowComponent implements ITooltipComponent {

    /**
     * @param progress the progress between 0.0f and 1.0f.
     */
    public ProgressArrowComponent(float progress) {
        this.progress = Mth.clamp(progress, 0.0f, 1.0f);
    }

    private final float progress;

    @Override
    public int getWidth() {
        return 22;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        // Draws the "empty" background arrow
        ctx.blitSprite(RenderType::guiTextured, WailaConstants.COMPONENT_TEXTURE, 255, 255, 0, 16, x, y, 22, 16);

        if (progress > 0) {
            // Draws the "full" foreground arrow based on the progress
            ctx.blitSprite(RenderType::guiTextured, WailaConstants.COMPONENT_TEXTURE, 255, 255, 0, 0, x, y, (int) (progress * 22) + 1, 16);
        }
    }

}
