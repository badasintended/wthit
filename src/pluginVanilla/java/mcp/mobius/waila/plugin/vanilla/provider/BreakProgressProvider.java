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

        var lineLength = 0f;

        if (config.getBoolean(Options.BREAKING_PROGRESS_BOTTOM_ONLY)) {
            lineLength = (rect.width - 2) * actualProgress;
        } else {
            lineLength = ((rect.width + rect.height - 4) * 2) * actualProgress;
        }

        if (lineLength >= 1) {
            var hLength = rect.width - 2;
            var vLength = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;

            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            ctx.hLine(x, x + (int) Math.min(lineLength, hLength) - 1, y, color);
            lineLength -= hLength;

            if (lineLength > 0) {
                x = rect.x + rect.width - 2;
                y = rect.y + rect.height - 2;
                ctx.vLine(x, y, y - (int) Math.min(lineLength, vLength) - 1, color);
                lineLength -= vLength;

                if (lineLength > 0) {
                    x = rect.x + rect.width - 2;
                    y = rect.y + 1;
                    ctx.hLine(x, x - (int) Math.min(lineLength, hLength) + 1, y, color);
                    lineLength -= hLength;

                    if (lineLength > 0) {
                        x = rect.x + 1;
                        y = rect.y + 1;
                        ctx.vLine(x, y, y + (int) Math.min(lineLength, vLength) + 1, color);
                    }
                }
            }
        }

        wasBreaking = isBreaking;
        lastProgress = actualProgress;
        lastTargetProgress = targetProgress;
        if (isInDelay) progressDelayTimer -= dt;
    }

}
