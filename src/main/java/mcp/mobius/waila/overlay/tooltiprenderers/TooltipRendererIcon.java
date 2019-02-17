package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.Dimension;

public class TooltipRendererIcon implements ITooltipRenderer {

    private final String type;
    private final int size = 8;

    public TooltipRendererIcon(String type) {
        this.type = type;
    }

    @Override
    public Dimension getSize(NBTTagCompound tag, ICommonAccessor accessor) {
        return new Dimension(size, size);
    }

    @Override
    public void draw(NBTTagCompound tag, ICommonAccessor accessor, int x, int y) {
        DisplayUtil.renderIcon(x, y, size, size, IconUI.bySymbol(type));
    }

}
