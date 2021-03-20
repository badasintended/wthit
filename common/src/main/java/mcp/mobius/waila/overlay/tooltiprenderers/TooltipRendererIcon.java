package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;

public class TooltipRendererIcon extends DisplayUtil implements ITooltipRenderer {

    private final String type;
    private final int size = 8;

    public TooltipRendererIcon(String type) {
        this.type = type;
    }

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return new Dimension(size, size);
    }

    @Override
    public void draw(MatrixStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        renderIcon(matrices, x, y, size, size, IconUI.bySymbol(type));
    }

}
