package mcp.mobius.waila.plugin.vanilla.provider;

import java.awt.Rectangle;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixin.MultiPlayerGameModeAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public enum BreakProgressProvider implements IEventListener {

    INSTANCE;

    boolean wasBreaking = false;
    float progressDelayTimer = 0;
    float lastProgress = 0;
    float lastTargetProgress = 0;

    @Override
    public void onAfterTooltipRender(PoseStack matrices, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.BREAKING_PROGRESS)) return;

        var gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        var gameModeAccess = (MultiPlayerGameModeAccess) gameMode;

        var dt = Minecraft.getInstance().getDeltaFrameTime();

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

        float lineLength;

        if (config.getBoolean(Options.BREAKING_PROGRESS_BOTTOM_ONLY)) {
            lineLength = (rect.width - 2) * actualProgress;
        } else {
            lineLength = ((rect.width + rect.height - 4) * 2) * actualProgress;
        }

        if (lineLength > 0) {
            var hLength = rect.width - 2;
            var vLength = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;

            var tesselator = Tesselator.getInstance();
            var buffer = tesselator.getBuilder();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            fill(matrices, buffer, x, y, x + Math.min(lineLength, hLength), y + 1, color);
            lineLength -= hLength;

            if (lineLength > 0) {
                x = rect.x + rect.width - 2;
                y = rect.y + rect.height - 2;
                fill(matrices, buffer, x, y, x + 1, y - Math.min(lineLength, vLength), color);
                lineLength -= vLength;

                if (lineLength > 0) {
                    x = rect.x + rect.width - 1;
                    y = rect.y + 1;
                    fill(matrices, buffer, x, y, x - Math.min(lineLength, hLength), y + 1, color);
                    lineLength -= hLength;

                    if (lineLength > 0) {
                        x = rect.x + 1;
                        y = rect.y + 2;
                        fill(matrices, buffer, x, y, x + 1, y + Math.min(lineLength, vLength), color);
                    }
                }
            }

            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }

        wasBreaking = isBreaking;
        lastProgress = actualProgress;
        lastTargetProgress = targetProgress;
        if (isInDelay) progressDelayTimer -= dt;
    }

    private void fill(PoseStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float x2, float y2, int color) {
        var matrix4f = matrices.last().pose();
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

        vertexConsumer.vertex(matrix4f, x1, y1, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y2, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y2, 0f).color(color).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y1, 0f).color(color).endVertex();
    }

}
