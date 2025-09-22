package mcp.mobius.waila.gui.hud;

import java.util.Random;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.util.WRenders;
import mcp.mobius.waila.util.DisplayUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public abstract class ComponentRenderer {

    private static final Random RANDOM = new Random();

    public abstract void render(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, int ch, DeltaTracker delta);

    private static @Nullable ComponentRenderer current = null;

    public static ComponentRenderer get() {
        if (current == null) current = Default.INSTANCE;
        return current;
    }

    public static void set(@Nullable ComponentRenderer value) {
        if (value == null) value = Default.INSTANCE;
        current = value;
    }

    public static class Default extends ComponentRenderer {

        public static final Default INSTANCE = new Default();

        @Override
        public void render(GuiGraphics ctx, ITooltipComponent component, int x, int y, int cw, int ch, DeltaTracker delta) {
            component.render(ctx, x, y, delta);

            if (WailaClient.showComponentBounds) {
                ctx.nextStratum();
                renderBounds(ctx, x, y, cw, ch, 1f);
            }
        }

        public static void renderBounds(GuiGraphics ctx, int x, int y, int cw, int ch, float v) {
            ctx.pose().pushMatrix();
            var scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            ctx.pose().scale(1 / scale, 1 / scale);

            var bx = Mth.floor(x * scale + 0.5);
            var by = Mth.floor(y * scale + 0.5);
            var bw = Mth.floor(cw * scale + 0.5);
            var bh = Mth.floor(ch * scale + 0.5);
            var color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), v);

            WRenders.state(ctx).submitGuiElement(new BoundsRenderState(new Matrix3x2f(ctx.pose()), new ScreenRectangle(bx, by, bw, bh), color));
            ctx.pose().popMatrix();
        }

    }

    private record BoundsRenderState(Matrix3x2f pose, ScreenRectangle bounds, int color) implements GuiElementRenderState {

        @Override
        public void buildVertices(VertexConsumer buf) {
            DisplayUtil.renderRectBorder(pose, buf, bounds.left(), bounds.top(), bounds.width(), bounds.height(), 1, color, color);
        }

        @Override
        public RenderPipeline pipeline() {
            return RenderPipelines.GUI;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return null;
        }

    }

}
