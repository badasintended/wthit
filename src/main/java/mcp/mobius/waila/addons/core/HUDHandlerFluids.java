package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HUDHandlerFluids implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerFluids();

    @Override
    public ItemStack getStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock() == Blocks.WATER)
            return new ItemStack(Items.WATER_BUCKET);
        else if (accessor.getBlock() == Blocks.LAVA)
            return new ItemStack(Items.LAVA_BUCKET);

        return ItemStack.EMPTY;
    }
}
