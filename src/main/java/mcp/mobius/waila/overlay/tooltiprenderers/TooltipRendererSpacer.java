package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.Dimension;

public class TooltipRendererSpacer implements ITooltipRenderer {

    @Override
    public Dimension getSize(NBTTagCompound data, ICommonAccessor accessor) {
        return new Dimension(data.getInt("width"), data.getInt("height"));
    }

    @Override
    public void draw(NBTTagCompound data, ICommonAccessor accessor, int x, int y) {
        // no-op
    }
}
