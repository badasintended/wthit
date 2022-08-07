package mcp.mobius.waila.plugin.vanilla.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ProgressComponent extends GuiComponent implements ITooltipComponent {

    private static final ResourceLocation SHEET = new ResourceLocation(WailaConstants.NAMESPACE, "textures/sprites.png");

    private final int currentValue;
    private final int maxValue;

    public ProgressComponent(int currentValue, int maxValue) {
        this.currentValue = currentValue;
        this.maxValue = maxValue;
    }

    @Override
    public int getWidth() {
        return 24;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SHEET);

        // Draws the "empty" background arrow
        blit(matrices, x + 1, y + 1, 0, 16, 22, 16, 22, 32);

        if (maxValue > 0) {
            int progress = (currentValue * 22) / maxValue;
            // Draws the "full" foreground arrow based on the progress
            blit(matrices, x + 1, y + 1, 0, 0, progress + 1, 16, 22, 32);
        }
    }

}
