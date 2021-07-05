package mcp.mobius.waila.plugin.vanilla.renderer;

import java.awt.Dimension;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import static mcp.mobius.waila.util.DisplayUtil.renderStack;

public class ItemRenderer implements ITooltipRenderer {

    private static final Supplier<Dimension> DIMENSION = Suppliers.memoize(() -> new Dimension(18, 18));

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
