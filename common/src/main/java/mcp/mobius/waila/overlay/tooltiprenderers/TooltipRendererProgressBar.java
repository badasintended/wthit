package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

public class TooltipRendererProgressBar extends DisplayUtil implements ITooltipRenderer {

    private static final Identifier SHEET = new Identifier(Waila.MODID, "textures/sprites.png");
    private static final Lazy<Dimension> DIMENSION = new Lazy<>(() -> new Dimension(26, 16));

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return DIMENSION.get();
    }

    @Override
    public void draw(MatrixStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("progress");

        bind(SHEET);

        // Draws the "empty" background arrow
        drawTexturedModalRect(matrices, x + 2, y, 0, 16, 22, 16, 22, 16);

        int maxValue = tag.getInt("total");
        if (maxValue > 0) {
            int progress = (currentValue * 22) / maxValue;
            // Draws the "full" foreground arrow based on the progress
            drawTexturedModalRect(matrices, x + 2, y, 0, 0, progress + 1, 16, progress + 1, 16);
        }
    }

}
