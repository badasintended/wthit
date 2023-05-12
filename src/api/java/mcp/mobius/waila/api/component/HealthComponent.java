package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/**
 * A component that renders a health bar.
 */
@ApiSide.ClientOnly
public class HealthComponent implements ITooltipComponent {

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
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        int filled = health / 2 - 1;
        int half = filled + health % 2;

        for (int i = iconCount - 1; i >= 0; i--) {
            int ix = x + ((i % lineWidth) * 8);
            int iy = y + ((i / lineWidth) * 3);

            ctx.blit(WailaHelper.GUI_ICONS_TEXTURE, ix, iy, 16, 0, 9, 9);
            if (i <= filled) {
                ctx.blit(WailaHelper.GUI_ICONS_TEXTURE, ix, iy, absorption ? 160 : 52, 0, 9, 9);
            } else if (i == half) {
                ctx.blit(WailaHelper.GUI_ICONS_TEXTURE, ix, iy, absorption ? 169 : 61, 0, 9, 9);
            }
        }
    }

}
