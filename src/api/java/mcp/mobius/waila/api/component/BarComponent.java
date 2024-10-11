package mcp.mobius.waila.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private static final float U0 = 22f / 256f;
    static final float U1 = 122f / 256f;
    static final float V0_BG = 0f / 256f;
    static final float V1_BG = HEIGHT / 256f;
    private static final float V0_FG = HEIGHT / 256f;
    private static final float V1_FG = 22f / 256f;
    private static final float UV_W = WIDTH / 256f;

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
        renderBar(ctx.pose(), x, y, WIDTH, V0_BG, U1, V1_BG, color);
        renderBar(ctx.pose(), x, y, WIDTH * ratio, V0_FG, U0 + (UV_W * ratio), V1_FG, color);
        renderText(ctx, text, x, y);
    }

    static void renderBar(
        PoseStack matrices,
        int x, int y, float w,
        float v0, float u1, float v1, int tint
    ) {
        matrices.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // TODO
//        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, WailaConstants.COMPONENT_TEXTURE);

        var a = WailaHelper.getAlpha(tint);
        var r = WailaHelper.getRed(tint);
        var g = WailaHelper.getGreen(tint);
        var b = WailaHelper.getBlue(tint);

        var tessellator = Tesselator.getInstance();
        var buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrices.last().pose(), x, y + HEIGHT, 0).setUv(U0, v1).setColor(r, g, b, a);
        buffer.addVertex(matrices.last().pose(), x + w, y + HEIGHT, 0).setUv(u1, v1).setColor(r, g, b, a);
        buffer.addVertex(matrices.last().pose(), x + w, y, 0).setUv(u1, v0).setColor(r, g, b, a);
        buffer.addVertex(matrices.last().pose(), x, y, 0).setUv(U0, v0).setColor(r, g, b, a);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
        matrices.popPose();
    }

    static void renderText(GuiGraphics ctx, Component text, int x, int y) {
        ctx.drawSpecial(bufferSource -> {
            var font = Minecraft.getInstance().font;
            var textWidth = font.width(text);
            var textX = x + Math.max((BarComponent.WIDTH - textWidth) / 2F, 0F);
            float textY = y + 2;

            font.drawInBatch8xOutline(text.getVisualOrderText(), textX, textY, 0xAAAAAA, 0x292929, ctx.pose().last().pose(), bufferSource, 0xf000f0);
        });
    }

}
