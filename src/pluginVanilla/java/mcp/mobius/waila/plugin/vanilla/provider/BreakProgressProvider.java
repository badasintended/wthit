package mcp.mobius.waila.plugin.vanilla.provider;

import java.awt.*;
import java.util.Objects;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixin.MultiPlayerGameModeAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public enum BreakProgressProvider implements IEventListener {

    INSTANCE;

    boolean wasBreaking = false;
    float progressDelayTimer = 0;
    float lastProgress = 0;
    float lastTargetProgress = 0;

    @Override
    public void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.BREAKING_PROGRESS)) return;

        var gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        var gameModeAccess = (MultiPlayerGameModeAccess) gameMode;

        var dt = Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks();

        var isBreaking = gameMode.isDestroying();
        var targetProgress = gameModeAccess.wthit_destroyProgress();
        if (!isBreaking && wasBreaking && progressDelayTimer <= 0f) {
            progressDelayTimer = 4f;
        }

        var isInDelay = progressDelayTimer > 0;
        if (isInDelay) {
            targetProgress = gameModeAccess.wthit_destroyDelay() > 0 ? 1f : lastTargetProgress;
        }

        var progressDiff = targetProgress - lastProgress;
        var progressChangeAmount = progressDiff * dt;
        var actualProgress = Mth.clamp(lastProgress + progressChangeAmount, 0f, 1f);

        final float[] lineLength = new float[1];

        if (config.getBoolean(Options.BREAKING_PROGRESS_BOTTOM_ONLY)) {
            lineLength[0] = (rect.width - 2) * actualProgress;
        } else {
            lineLength[0] = ((rect.width + rect.height - 4) * 2) * actualProgress;
        }

        if (lineLength[0] > 0) ctx.drawSpecial(bufferSource -> {
            var hLength = rect.width - 2;
            var vLength = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;

            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            fill(ctx, bufferSource, x, y, x + Math.min(lineLength[0], hLength), y + 1, color);
            lineLength[0] -= hLength;

            if (lineLength[0] > 0) {
                x = rect.x + rect.width - 2;
                y = rect.y + rect.height - 2;
                fill(ctx, bufferSource, x, y, x + 1, y - Math.min(lineLength[0], vLength), color);
                lineLength[0] -= vLength;

                if (lineLength[0] > 0) {
                    x = rect.x + rect.width - 1;
                    y = rect.y + 1;
                    fill(ctx, bufferSource, x, y, x - Math.min(lineLength[0], hLength), y + 1, color);
                    lineLength[0] -= hLength;

                    if (lineLength[0] > 0) {
                        x = rect.x + 1;
                        y = rect.y + 2;
                        fill(ctx, bufferSource, x, y, x + 1, y + Math.min(lineLength[0], vLength), color);
                    }
                }
            }
        });

        wasBreaking = isBreaking;
        lastProgress = actualProgress;
        lastTargetProgress = targetProgress;
        if (isInDelay) progressDelayTimer -= dt;
    }

    private void fill(GuiGraphics ctx, MultiBufferSource bufferSource, float x1, float y1, float x2, float y2, int color) {
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

        var vertexConsumer = bufferSource.getBuffer(RenderType.gui());
        vertexConsumer.addVertex(matrix4f, x1, y1, 0f).setColor(color);
        vertexConsumer.addVertex(matrix4f, x1, y2, 0f).setColor(color);
        vertexConsumer.addVertex(matrix4f, x2, y2, 0f).setColor(color);
        vertexConsumer.addVertex(matrix4f, x2, y1, 0f).setColor(color);
    }

}
