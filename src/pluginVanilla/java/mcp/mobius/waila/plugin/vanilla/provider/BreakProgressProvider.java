package mcp.mobius.waila.plugin.vanilla.provider;

import java.awt.*;
import java.util.Objects;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixed.MMultiPlayerGameMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public enum BreakProgressProvider implements IEventListener {

    INSTANCE;

    static final int FADE_TICK = 3;

    boolean wasBreaking = false;
    float targetProgress = 0;
    float lastProgress = 0;

    boolean fadeFinish = false;
    int fadeTicks = 0;
    float fade = 0;

    @Override
    public void onTick(IPluginConfig config) {
        targetProgress = 0;
        if (fadeFinish) {
            fade = 0;
            lastProgress = 0f;
            fadeFinish = false;
        }

        if (!config.getBoolean(Options.BREAKING_PROGRESS)) return;
        var gameMode = (MMultiPlayerGameMode) Objects.requireNonNull(Minecraft.getInstance().gameMode);
        targetProgress = gameMode.wthit_destroyProgress();

        var isBreaking = targetProgress > 0;
        if (wasBreaking && !isBreaking) {
            fadeTicks = FADE_TICK;
            fade = fadeTicks;
            if (gameMode.wthit_wasDestroyed()) targetProgress = 1;
        }

        wasBreaking = isBreaking;
        if (fadeTicks > 0) {
            fadeTicks--;
            if (fadeTicks == 0) fadeFinish = true;
        }
    }

    @Override
    public void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        var dt = Minecraft.getInstance().getDeltaFrameTime();

        var progress = lastProgress;
        if (targetProgress > lastProgress) {
            var progressChange = (targetProgress - lastProgress) * dt;
            progress = Mth.clamp(lastProgress + progressChange, 0f, 1f);
        }

        lastProgress = progress;
        if (progress <= 0f && fade <= 0) return;

        var bottomOnly = config.getBoolean(Options.BREAKING_PROGRESS_BOTTOM_ONLY);
        var lineLength = bottomOnly
            ? (rect.width - 2) * progress
            : (((rect.width - 2) + (rect.height - 2)) * 2) * progress;

        if (lineLength > 0) {
            var hLength = rect.width - 2;
            var vLength = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;

            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            var alpha = fade > 0 ? fade / FADE_TICK : 1.0f;
            var a = (int) (alpha * ((color >> 24) & 0xFF));
            color = (color & 0x00FFFFFF) | (a << 24);

            fill(ctx, x, y, x + Math.min(lineLength, hLength), y + 1, color);
            lineLength -= hLength;

            if (lineLength > 0) {
                x = rect.x + rect.width - 2;
                y = rect.y + rect.height - 2;
                fill(ctx, x, y, x + 1, y - Math.min(lineLength, vLength), color);
                lineLength -= vLength;

                if (lineLength > 0) {
                    x = rect.x + rect.width - 1;
                    y = rect.y + 1;
                    fill(ctx, x, y, x - Math.min(lineLength, hLength), y + 1, color);
                    lineLength -= hLength;

                    if (lineLength > 0) {
                        x = rect.x + 1;
                        y = rect.y + 2;
                        fill(ctx, x, y, x + 1, y + Math.min(lineLength, vLength), color);
                    }
                }
            }

            ctx.flush();
        }

        if (fade > 0) fade = Math.max(fade - dt, 0f);
    }

    private void fill(GuiGraphics ctx, float x1, float y1, float x2, float y2, int color) {
        var matrix4f = ctx.pose().last().pose();
        if (x1 < x2) {
            var o = x1;
            x1 = x2;
            x2 = o;
        }

        if (y1 < y2) {
            var o = y1;
            y1 = y2;
            y2 = o;
        }

        var vertexConsumer = ctx.bufferSource().getBuffer(RenderType.gui());
        vertexConsumer.vertex(matrix4f, x1, y1, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y2, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y2, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y1, 0f).color(color).endVertex();
    }

}
