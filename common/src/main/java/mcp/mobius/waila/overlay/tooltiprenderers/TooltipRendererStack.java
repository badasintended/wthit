package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Lazy;

import static mcp.mobius.waila.overlay.DisplayUtil.renderStack;

public class TooltipRendererStack implements ITooltipRenderer {

    private static final Lazy<Dimension> DIMENSION = new Lazy<>(() -> new Dimension(18, 18));

    @Override
    public Dimension getSize(NbtCompound tag, ICommonAccessor accessor) {
        return DIMENSION.get();
    }

    @Override
    public void draw(MatrixStack matrices, NbtCompound tag, ICommonAccessor accessor, int x, int y) {
        if (tag.getInt("Count") > 0) {
            renderStack(x, y, ItemStack.fromNbt(tag));
        }
    }

}
