package mcp.mobius.waila.service;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import mcp.mobius.waila.gui.hud.ComponentRenderer;
import mcp.mobius.waila.gui.render.DisgustingRenderState;
import mcp.mobius.waila.gui.render.OutlinedTextRenderer;
import mcp.mobius.waila.mixin.GuiGraphicsAccess;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public abstract class ClientApiService implements IClientApiService {

    @Override
    public GuiRenderState getRenderState(GuiGraphics ctx) {
        return ((GuiGraphicsAccess) ctx).wthit_guiRenderState();
    }

    @Override
    public void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, DeltaTracker delta) {
        ComponentRenderer.get().render(ctx, component, x, y, component.getWidth(), component.getHeight(), delta);
    }

    @Override
    public void fillGradient(Matrix3x2f matrix, VertexConsumer buf, int x, int y, float z, int w, int h, int start, int end) {
        DisplayUtil.fillGradient(matrix, buf, x, y, z, w, h, start, end);
    }

    @Override
    public void renderRectBorder(Matrix3x2f matrix, VertexConsumer buf, int x, int y, float z, int w, int h, int s, int gradStart, int gradEnd) {
        DisplayUtil.renderRectBorder(matrix, buf, x, y, z, w, h, s, gradStart, gradEnd);
    }

    @Override
    public PictureInPictureRenderState pipOutlinedText(Component text, int x, int y, float scale, @Nullable ScreenRectangle scissorArea) {
        return OutlinedTextRenderer.state(text, x, y, scale, scissorArea);
    }

    @Override
    public GuiElementRenderState guiDisgusting(RenderPipeline pipeline, @Nullable ResourceLocation rl, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds, DisgustingRenderStateImpl impl) {
        return DisgustingRenderState.of(pipeline, rl, scissorArea, bounds, impl);
    }

}
