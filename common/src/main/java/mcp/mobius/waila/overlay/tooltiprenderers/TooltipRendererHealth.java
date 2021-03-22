package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;

import static mcp.mobius.waila.overlay.DisplayUtil.renderIcon;

public class TooltipRendererHealth implements ITooltipRenderer {

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        float maxHearts = Waila.getConfig().get().getGeneral().getMaxHeartsPerLine();
        float maxHealth = tag.getFloat("max");

        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));
        int lineCount = (int) (Math.ceil(maxHealth / maxHearts));

        return new Dimension(8 * heartsPerLine, 10 * lineCount);
    }

    @Override
    public void draw(MatrixStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        float maxHearts = Waila.getConfig().get().getGeneral().getMaxHeartsPerLine();
        float health = tag.getFloat("health");
        float maxHealth = tag.getFloat("max");

        int heartCount = MathHelper.ceil(maxHealth);
        int heartsPerLine = (int) (Math.min(maxHearts, Math.ceil(maxHealth)));

        int xOffset = 0;
        for (int i = 1; i <= heartCount; i++) {
            if (i <= MathHelper.floor(health)) {
                renderIcon(matrices, x + xOffset, y, 8, 8, IconUI.HEART);
                xOffset += 8;
            }

            if ((i > health) && (i < health + 1)) {
                renderIcon(matrices, x + xOffset, y, 8, 8, IconUI.HALF_HEART);
                xOffset += 8;
            }

            if (i >= health + 1) {
                renderIcon(matrices, x + xOffset, y, 8, 8, IconUI.EMPTY_HEART);
                xOffset += 8;
            }

            if (i % heartsPerLine == 0) {
                y += 10;
                xOffset = 0;
            }

        }
    }

}
