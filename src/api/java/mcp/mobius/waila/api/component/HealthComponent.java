package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

/**
 * Component that renders a health bar.
 */
@ApiSide.ClientOnly
public class HealthComponent implements ITooltipComponent {

    private static final ResourceLocation SPRITE_CONTAINER = new ResourceLocation("hud/heart/container");
    private static final ResourceLocation SPRITE_NORMAL_FULL = new ResourceLocation("hud/heart/full");
    private static final ResourceLocation SPRITE_NORMAL_HALF = new ResourceLocation("hud/heart/half");
    private static final ResourceLocation SPRITE_ABSORBING_FULL = new ResourceLocation("hud/heart/absorbing_full");
    private static final ResourceLocation SPRITE_ABSORBING_HALF = new ResourceLocation("hud/heart/absorbing_half");

    /**
     * @param health     the health point, 1 full icon represent 2 hp
     * @param maxHealth  the max health point
     * @param maxPerLine the max icon per line until it get wrapped into multiple
     * @param absorption indicates if the bar is absorption health bar
     */
    public HealthComponent(float health, float maxHealth, int maxPerLine, boolean absorption) {
        this.health = Mth.ceil(health);
        this.maxHealth = Mth.ceil(maxHealth);
        this.iconCount = Mth.positiveCeilDiv(Mth.ceil(Math.max(health, maxHealth)), 2);
        this.lineWidth = Math.min(iconCount, maxPerLine);
        this.absorption = absorption;
    }

    private final int health, maxHealth;
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
    public @Nullable Component getNarration() {
        return Component.translatable(Tl.Tts.Component.HEALTH, health, maxHealth);
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, float delta) {
        var filled = health / 2 - 1;
        var half = filled + health % 2;

        for (var i = iconCount - 1; i >= 0; i--) {
            var ix = x + ((i % lineWidth) * 8);
            var iy = y + ((i / lineWidth) * 3);

            ctx.blitSprite(SPRITE_CONTAINER, ix, iy, 9, 9);
            if (i <= filled) {
                ctx.blitSprite(absorption ? SPRITE_ABSORBING_FULL : SPRITE_NORMAL_FULL, ix, iy, 9, 9);
            } else if (i == half) {
                ctx.blitSprite(absorption ? SPRITE_ABSORBING_HALF : SPRITE_NORMAL_HALF, ix, iy, 9, 9);
            }
        }
    }

}
