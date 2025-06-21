package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Component that renders a health bar.
 */
@ApiSide.ClientOnly
public class HealthComponent implements ITooltipComponent {

    private static final ResourceLocation SPRITE_CONTAINER = ResourceLocation.withDefaultNamespace("hud/heart/container");
    private static final ResourceLocation SPRITE_NORMAL_FULL = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation SPRITE_NORMAL_HALF = ResourceLocation.withDefaultNamespace("hud/heart/half");
    private static final ResourceLocation SPRITE_ABSORBING_FULL = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full");
    private static final ResourceLocation SPRITE_ABSORBING_HALF = ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half");

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
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var filled = health / 2 - 1;
        var half = filled + health % 2;

        for (var i = iconCount - 1; i >= 0; i--) {
            var ix = x + ((i % lineWidth) * 8);
            var iy = y + ((i / lineWidth) * 3);

            ctx.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_CONTAINER, ix, iy, 9, 9);
            if (i <= filled) {
                ctx.blitSprite(RenderPipelines.GUI_TEXTURED, absorption ? SPRITE_ABSORBING_FULL : SPRITE_NORMAL_FULL, ix, iy, 9, 9);
            } else if (i == half) {
                ctx.blitSprite(RenderPipelines.GUI_TEXTURED, absorption ? SPRITE_ABSORBING_HALF : SPRITE_NORMAL_HALF, ix, iy, 9, 9);
            }
        }
    }

}
