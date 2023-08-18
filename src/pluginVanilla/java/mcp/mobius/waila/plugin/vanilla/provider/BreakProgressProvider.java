package mcp.mobius.waila.plugin.vanilla.provider;

import java.awt.Rectangle;
import java.util.Objects;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixin.MultiPlayerGameModeAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public enum BreakProgressProvider implements IEventListener {

    INSTANCE;

    @Override
    public void onAfterTooltipRender(GuiGraphics ctx, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        var gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        var gameModeAccess = (MultiPlayerGameModeAccess) gameMode;

        if (config.getBoolean(Options.BREAKING_PROGRESS) && gameMode.isDestroying()) {
            var color = config.getInt(Options.BREAKING_PROGRESS_COLOR);
            int lineLenght;

            if (config.getBoolean(Options.BREAKING_PROGRESS_BOTTOM_ONLY)) {
                lineLenght = (int) ((rect.width - 2) * gameModeAccess.wthit_destroyProgress());
            } else {
                lineLenght = (int) (((rect.width + rect.height - 4) * 2) * gameModeAccess.wthit_destroyProgress());
            }

            var hLenght = rect.width - 2;
            var vLenght = rect.height - 4;

            var x = rect.x + 1;
            var y = rect.y + rect.height - 2;
            ctx.fill(x, y, x + Math.min(lineLenght, hLenght), y + 1, color);
            lineLenght -= hLenght;

            if (lineLenght <= 0) {
                return;
            }

            x = rect.x + rect.width - 2;
            y = rect.y + rect.height - 2;
            ctx.fill(x, y, x + 1, y - Math.min(lineLenght, vLenght), color);
            lineLenght -= vLenght;

            if (lineLenght <= 0) {
                return;
            }

            x = rect.x + rect.width - 1;
            y = rect.y + 1;
            ctx.fill(x, y, x - Math.min(lineLenght, hLenght), y + 1, color);
            lineLenght -= hLenght;

            if (lineLenght <= 0) {
                return;
            }

            x = rect.x + 1;
            y = rect.y + 2;
            ctx.fill(x, y, x + 1, y + Math.min(lineLenght, vLenght), color);
        }
    }

}
