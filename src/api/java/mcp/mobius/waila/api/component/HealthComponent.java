package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * Component that renders a health bar.
 */
@ApiSide.ClientOnly
public class HealthComponent extends GuiComponent implements ITooltipComponent {

    /**
     * @param health     the health point, 1 full icon represent 2 hp
     * @param maxHealth  the max health point
     * @param maxPerLine the max icon per line until it get wrapped into multiple
     * @param absorption indicates if the bar is absorption health bar
     */
    public HealthComponent(float health, float maxHealth, int maxPerLine, boolean absorption) {
        this.health = Mth.ceil(health);
        this.iconCount = Mth.positiveCeilDiv(Mth.ceil(Math.max(health, maxHealth)), 2);
        this.lineWidth = Math.min(iconCount, maxPerLine);
        this.absorption = absorption;
    }

    private final int health;
    private final int iconCount;
    private final int lineWidth;
    private final boolean absorption;

    @Override
    public int getWidth() {
        return (lineWidth * 8) + 1;
    }

    @Override
    public int getHeight() {
        return (Mth.positiveCeilDiv(iconCount, lineWidth) * 3) + 6;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var filled = health / 2 - 1;
        var half = filled + health % 2;

        for (var i = iconCount - 1; i >= 0; i--) {
            var ix = x + ((i % lineWidth) * 8);
            var iy = y + ((i / lineWidth) * 3);

            blit(matrices, ix, iy, 16, 0, 9, 9);
            if (i <= filled) {
                blit(matrices, ix, iy, absorption ? 160 : 52, 0, 9, 9);
            } else if (i == half) {
                blit(matrices, ix, iy, absorption ? 169 : 61, 0, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }

}
