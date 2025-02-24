package mcp.mobius.waila.gui.hud;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.ITooltipComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import static mcp.mobius.waila.util.DisplayUtil.renderRectBorder;

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
                renderBounds(ctx, x, y, cw, ch, 1f);
            }
        }

        public static void renderBounds(GuiGraphics ctx, int x, int y, int cw, int ch, float v) {
            ctx.pose().pushPose();
            var scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
            ctx.pose().scale(1 / scale, 1 / scale, 1);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            var buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            var bx = Mth.floor(x * scale + 0.5);
            var by = Mth.floor(y * scale + 0.5);
            var bw = Mth.floor(cw * scale + 0.5);
            var bh = Mth.floor(ch * scale + 0.5);
            var color = (0xFF << 24) + Mth.hsvToRgb(RANDOM.nextFloat(), RANDOM.nextFloat(), v);
            renderRectBorder(ctx.pose().last().pose(), buf, bx, by, bw, bh, 1, color, color);
            BufferUploader.drawWithShader(buf.buildOrThrow());

            ctx.pose().popPose();
        }

    }

}
