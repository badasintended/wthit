package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.api.util.WRenders;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3x2f;

/**
 * Component that renders a bar with a texture as the foreground.
 */
public class SpriteBarComponent implements ITooltipComponent {

    public SpriteBarComponent(float ratio, ResourceLocation texture, float u0, float u1, float v0, float v1, int regionWidth, int regionHeight, int tint, Component text) {
        this.ratio = ratio;
        this.texture = texture;
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.spriteTint = tint;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.text = text;
    }

    public SpriteBarComponent(float ratio, TextureAtlasSprite sprite, int regionWidth, int regionHeight, int tint, Component text) {
        this(ratio, sprite.atlasLocation(), sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), regionWidth, regionHeight, tint, text);
    }

    private final float ratio;
    private final ResourceLocation texture;
    private final float u0, u1, v0, v1;
    private final int spriteTint;
    private final int regionWidth;
    private final int regionHeight;
    private final Component text;

    @Override
    public int getWidth() {
        return Math.max(Minecraft.getInstance().font.width(text), BarComponent.WIDTH);
    }

    @Override
    public int getHeight() {
        return BarComponent.HEIGHT;
    }

    @Override
    public void render(GuiGraphics ctx, int x, int y, DeltaTracker delta) {
        var ps = ctx.pose();

        BarComponent.renderBar(ctx, x, y, BarComponent.WIDTH, BarComponent.V0_BG, 0xFFAAAAAA);

        var mw = (int) (BarComponent.WIDTH * ratio);
        if (mw > 0) {
            var mx = x + mw;
            var my = y + BarComponent.HEIGHT;
            ctx.enableScissor(x + 1, y + 1, mx - 1, my - 1);

            ps.pushMatrix();
            var pose = new Matrix3x2f(ps);

            WRenders.state(ctx).submitGuiElement(IClientApiService.INSTANCE.guiDisgusting(RenderPipelines.GUI_TEXTURED, texture, WRenders.scissor(ctx), new ScreenRectangle(x, y, mw, BarComponent.HEIGHT), (buffer, z) -> {
                for (var px1 = x; px1 < mx; px1 += regionWidth) {
                    var px2 = px1 + regionWidth;

                    for (var py1 = y; py1 < my; py1 += regionHeight) {
                        var py2 = py1 + regionHeight;

                        buffer.addVertexWith2DPose(pose, px1, py2, z).setUv(u0, v1).setColor(spriteTint);
                        buffer.addVertexWith2DPose(pose, px2, py2, z).setUv(u1, v1).setColor(spriteTint);
                        buffer.addVertexWith2DPose(pose, px2, py1, z).setUv(u1, v0).setColor(spriteTint);
                        buffer.addVertexWith2DPose(pose, px1, py1, z).setUv(u0, v0).setColor(spriteTint);
                    }
                }
            }));

            ps.popMatrix();
            ctx.disableScissor();
        }

        ctx.nextStratum();
        BarComponent.renderText(ctx, text, x, y);
    }

}
