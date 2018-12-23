package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.nbt.CompoundTag;

import java.awt.Dimension;

public class TooltipRendererIcon implements IWailaTooltipRenderer {

    private final String type;
    private final int size = 8;

    public TooltipRendererIcon(String type) {
        this.type = type;
    }

    @Override
    public Dimension getSize(CompoundTag tag, IWailaCommonAccessor accessor) {
        return new Dimension(size, size);
    }

    @Override
    public void draw(CompoundTag tag, IWailaCommonAccessor accessor, int x, int y) {
        DisplayUtil.renderIcon(x, y, size, size, IconUI.bySymbol(type));
    }

}
