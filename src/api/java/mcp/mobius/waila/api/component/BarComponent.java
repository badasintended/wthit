package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * A component that renders a colored bar.
 */
@ApiSide.ClientOnly
public class BarComponent extends GuiComponent implements ITooltipComponent {

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

    private static final float U0 = 22f / 256f;
    private static final float U1 = 122f / 256f;
    private static final float V0_BG = 0f / 256f;
    private static final float V1_BG = 11f / 256f;
    private static final float V0_FG = 11f / 256f;
    private static final float V1_FG = 22f / 256f;
    private static final float WIDTH = 100f / 256f;

    private final float ratio;
    private final int color;
    private final Component text;

    @Override
    public int getWidth() {
        return Math.max(Minecraft.getInstance().font.width(text), 100);
    }

    @Override
    public int getHeight() {
        return 11;
    }

    @Override
    public void render(PoseStack matrices, int x, int y, float delta) {
        WailaHelper.renderTintedTexture(matrices, WailaConstants.COMPONENT_TEXTURE, x, y, 100, 11, U0, V0_BG, U1, V1_BG, color);
        WailaHelper.renderTintedTexture(matrices, WailaConstants.COMPONENT_TEXTURE, x, y, 100 * ratio, 11, U0, V0_FG, U0 + (WIDTH * ratio), V1_FG, color);

        double luminance = WailaHelper.getLuminance(color);
        int overlay = 0;

        if (luminance < 0.25)
            overlay = 0x08FFFFFF;
        else if (luminance > 0.90)
            overlay = 0x80000000;
        else if (luminance > 0.80)
            overlay = 0x70000000;
        else if (luminance > 0.70)
            overlay = 0x60000000;
        else if (luminance > 0.60)
            overlay = 0x50000000;
        else if (luminance > 0.50)
            overlay = 0x40000000;

        if (overlay != 0) {
            fill(matrices, x, y, x + 100, y + 11, overlay);
        }

        int textWidth = Minecraft.getInstance().font.width(text);
        float textX = x + Math.max((100 - textWidth) / 2F, 0F);
        float textY = y + 2;
        Minecraft.getInstance().font.draw(matrices, text, textX, textY, 0xFFAAAAAA);
    }

}
