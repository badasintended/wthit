package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * Component that renders a colored bar.
 */
@ApiSide.ClientOnly
public class BarComponent implements ITooltipComponent {

    /**
     * @param ratio the ratio of the filled bar between 0.0f and 1.0f
     * @param color the bar color, <b>0xAARRGGBB</b>
     */
    public BarComponent(float ratio, int color) {
        this(ratio, color, CommonComponents.EMPTY);
    }

    /**
     * @param ratio the ratio of the filled bar between 0.0f and 1.0f
     * @param color the bar color, <b>0xAARRGGBB</b>
     * @param text  the text that will be shown in the middle of the bar
     */
    public BarComponent(float ratio, int color, String text) {
        this(ratio, color, Component.literal(text));
    }

    /**
     * @param ratio the ratio of the filled bar between 0.0f and 1.0f
     * @param color the bar color, <b>0xAARRGGBB</b>
     * @param text  the text that will be shown in the middle of the bar
     */
    public BarComponent(float ratio, int color, Component text) {
        this.ratio = ratio;
        this.color = color;
        this.text = text;
    }

    static final int WIDTH = 100;
    static final int HEIGHT = 11;
    private static final float U0 = 22f;
    static final float V0_BG = 0f;
    private static final float V0_FG = HEIGHT;

    private final float ratio;
    private final int color;
    private final Component text;

    @Override
    public int getWidth() {
        return Math.max(Minecraft.getInstance().font.width(text), WIDTH);
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        renderBar(ctx, x, y, WIDTH, V0_BG, color);
        renderBar(ctx, x, y, (int) (WIDTH * ratio), V0_FG, color);

        ctx.nextStratum();
        renderText(ctx, text, x, y);
    }

    static void renderBar(
        GuiGraphics ctx,
        int x, int y, int w,
        float v0, int tint
    ) {
        ctx.blit(RenderPipelines.GUI_TEXTURED, WailaConstants.COMPONENT_TEXTURE, x, y, U0, v0, w, HEIGHT, 256, 256, tint);
    }

    static void renderText(GuiGraphics ctx, Component text, int x, int y) {
        var font = Minecraft.getInstance().font;
        var textWidth = font.width(text);
        var textX = x + (int) Math.max((BarComponent.WIDTH - textWidth) / 2F, 0F);
        var textY = y + 2;

        var formatted = text.getVisualOrderText();
        for (var i = -1; i <= 1; i++) {
            for (var j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    ctx.drawString(font, formatted, textX + i, textY + j, 0xFF292929, false);
                }
            }
        }
        ctx.drawString(font, formatted, textX, textY, 0xFFAAAAAA, false);
    }

}
