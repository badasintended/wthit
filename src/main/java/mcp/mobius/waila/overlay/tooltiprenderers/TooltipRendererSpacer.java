package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import net.minecraft.nbt.CompoundTag;

import java.awt.Dimension;

public class TooltipRendererSpacer implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(CompoundTag data, IWailaCommonAccessor accessor) {
        return new Dimension(data.getInt("width"), data.getInt("height"));
    }

    @Override
    public void draw(CompoundTag data, IWailaCommonAccessor accessor, int x, int y) {
        // no-op
    }
}
