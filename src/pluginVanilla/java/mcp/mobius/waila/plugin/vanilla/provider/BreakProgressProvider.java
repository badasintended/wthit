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
        var dt = Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks();

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

        if (lineLength >= 1) {
            var hLength = rect.width - 2;
            var vLength = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;

            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            var alpha = fade > 0 ? fade / FADE_TICK : 1.0f;
            var a = (int) (alpha * ((color >> 24) & 0xFF));
            color = (color & 0x00FFFFFF) | (a << 24);

            ctx.hLine(x, x + Math.round(Math.min(lineLength, hLength)) - 1, y, color);
            lineLength -= hLength;

            if (lineLength > 0) {
                x = rect.x + rect.width - 2;
                y = rect.y + rect.height - 2;
                ctx.vLine(x, y, y - Math.round(Math.min(lineLength, vLength)) - 1, color);
                lineLength -= vLength;

                if (lineLength > 0) {
                    x = rect.x + rect.width - 2;
                    y = rect.y + 1;
                    ctx.hLine(x, x - Math.round(Math.min(lineLength, hLength)) + 1, y, color);
                    lineLength -= hLength;

                    if (lineLength > 0) {
                        x = rect.x + 1;
                        y = rect.y + 1;
                        ctx.vLine(x, y, y + Math.round(Math.min(lineLength, vLength)) + 1, color);
                    }
                }
            }
        }

        if (fade > 0) fade = Math.max(fade - dt, 0f);
    }

}
