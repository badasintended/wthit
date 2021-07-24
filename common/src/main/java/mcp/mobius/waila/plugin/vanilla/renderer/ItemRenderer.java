package mcp.mobius.waila.plugin.vanilla.renderer;

import java.awt.Dimension;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.ITooltipRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import static mcp.mobius.waila.util.DisplayUtil.renderStack;

public class ItemRenderer implements ITooltipRenderer {

    private static final Supplier<Dimension> DIMENSION = Suppliers.memoize(() -> new Dimension(18, 18));

    @Override
    public Dimension getSize(CompoundTag tag, ICommonAccessor accessor) {
        return DIMENSION.get();
    }

    @Override
    public void draw(PoseStack matrices, CompoundTag tag, ICommonAccessor accessor, int x, int y) {
        if (tag.getInt("Count") > 0) {
            renderStack(x, y, ItemStack.of(tag));
        }
    }

}
