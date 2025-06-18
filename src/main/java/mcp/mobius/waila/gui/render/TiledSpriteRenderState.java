package mcp.mobius.waila.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record TiledSpriteRenderState(
    Matrix3x2f pose,
    TextureSetup textureSetup,
    float u0, float u1, float v0, float v1,
    int spriteTint,
    int x0, int x1, int y0, int y1,
    int regionWidth,
    int regionHeight,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {


    public TiledSpriteRenderState(Matrix3x2f pose, TextureSetup textureSetup, float u0, float u1, float v0, float v1, int spriteTint, int x0, int x1, int y0, int y1, int regionWidth, int regionHeight, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
        this.pose = pose;
        this.textureSetup = textureSetup;
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.spriteTint = spriteTint;
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    @Override
    public void buildVertices(VertexConsumer buffer, float z) {
        for (var px1 = x0; px1 < x1; px1 += regionWidth) {
            var px2 = px1 + regionWidth;

            for (var py1 = y0; py1 < y1; py1 += regionHeight) {
                var py2 = py1 + regionHeight;

                buffer.addVertexWith2DPose(pose, px1, py2, z).setUv(u0, v1).setColor(spriteTint);
                buffer.addVertexWith2DPose(pose, px2, py2, z).setUv(u1, v1).setColor(spriteTint);
                buffer.addVertexWith2DPose(pose, px2, py1, z).setUv(u1, v0).setColor(spriteTint);
                buffer.addVertexWith2DPose(pose, px1, py1, z).setUv(u0, v0).setColor(spriteTint);
            }
        }
    }

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.GUI_TEXTURED;
    }

}
