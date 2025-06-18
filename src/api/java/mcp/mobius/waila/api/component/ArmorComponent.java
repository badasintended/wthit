package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Component that renders an armor bar.
 */
@ApiSide.ClientOnly
public class ArmorComponent implements ITooltipComponent {

    private static final ResourceLocation SPRITE_FULL = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final ResourceLocation SPRITE_HALF = ResourceLocation.withDefaultNamespace("hud/armor_half");

    /**
     * @param armor      the armor points, 1 full icon represent 2 points
     * @param maxPerLine the max icon per line until it get wrapped into multiple
     */
    public ArmorComponent(int armor, int maxPerLine) {
        this.armor = armor;
        this.iconCount = Mth.positiveCeilDiv(armor, 2);
        this.lineWidth = Math.min(iconCount, maxPerLine);
    }

    private final int armor;
    private final int iconCount;
    private final int lineWidth;

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
        var filled = armor / 2 - 1;
        var half = filled + armor % 2;

        for (var i = iconCount - 1; i >= 0; i--) {
            var ix = x + ((i % lineWidth) * 8);
            var iy = y + ((i / lineWidth) * 3);

            if (i <= filled) {
                ctx.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_FULL, ix, iy, 9, 9);
            } else if (i == half) {
                ctx.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HALF, ix, iy, 9, 9);
            }
        }
    }

}
