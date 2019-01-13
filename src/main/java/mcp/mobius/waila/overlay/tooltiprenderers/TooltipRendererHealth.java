package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;

import java.awt.Dimension;

public class TooltipRendererHealth implements ITooltipRenderer {

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        float maxHearts = Waila.CONFIG.get().getGeneral().getMaxHealthForRender();
        float maxHealth = tag.getFloat("max");

        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));
        int lineCount = (int) (Math.ceil(maxHealth / maxHearts));

        return new Dimension(8 * heartsPerLine, 10 * lineCount - 3);
    }

    @Override
    public void draw(CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        float maxHearts = Waila.CONFIG.get().getGeneral().getMaxHealthForRender();
        float health = tag.getFloat("health");
        float maxHealth = tag.getFloat("max");

        int heartCount = MathHelper.ceil(maxHealth);
        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));

        for (int i = 1; i <= heartCount; i++) {
            if (i <= MathHelper.floor(health)) {
                DisplayUtil.renderIcon(x, y, 8, 8, IconUI.HEART);
                x += 8;
            }

            if ((i > health) && (i < health + 1)) {
                DisplayUtil.renderIcon(x, y, 8, 8, IconUI.HALF_HEART);
                x += 8;
            }

            if (i >= health + 1) {
                DisplayUtil.renderIcon(x, y, 8, 8, IconUI.EMPTY_HEART);
                x += 8;
            }

            if (i % heartsPerLine == 0) {
                y += 10;
                x = 0;
            }

        }
    }
}
