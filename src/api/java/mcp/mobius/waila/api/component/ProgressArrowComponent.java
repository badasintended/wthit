package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * A component that renders a furnace-like progress arrow.
 */
@ApiSide.ClientOnly
public class ProgressArrowComponent extends GuiComponent implements ITooltipComponent {

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
    public void render(PoseStack matrices, int x, int y, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WailaConstants.COMPONENT_TEXTURE);

        // Draws the "empty" background arrow
        blit(matrices, x, y, 0, 16, 22, 16);

        if (progress > 0) {
            // Draws the "full" foreground arrow based on the progress
            blit(matrices, x, y, 0, 0, (int) (progress * 22) + 1, 16);
        }
    }

}
