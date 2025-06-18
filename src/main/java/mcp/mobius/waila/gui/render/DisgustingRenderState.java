package mcp.mobius.waila.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record DisgustingRenderState(
    RenderPipeline pipeline,
    TextureSetup textureSetup,
    @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds,
    IClientApiService.DisgustingRenderStateImpl impl
) implements GuiElementRenderState {

    public static DisgustingRenderState of(RenderPipeline pipeline, @Nullable ResourceLocation rl, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds, IClientApiService.DisgustingRenderStateImpl impl) {
        var texture = rl == null ? TextureSetup.noTexture() : TextureSetup.singleTexture(Minecraft.getInstance().getTextureManager().getTexture(rl).getTextureView());
        return new DisgustingRenderState(pipeline, texture, scissorArea, bounds, impl);
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer, float z) {
        impl.buildVertices(vertexConsumer, z);
    }

}
