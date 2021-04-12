package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;

public class TooltipRendererSpacer implements ITooltipRenderer {

    @Override
    public Dimension getSize(NbtCompound data, ICommonAccessor accessor) {
        return new Dimension(data.getInt("width"), data.getInt("height"));
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound data, ICommonAccessor accessor, int x, int y) {
        // no-op
    }

}
