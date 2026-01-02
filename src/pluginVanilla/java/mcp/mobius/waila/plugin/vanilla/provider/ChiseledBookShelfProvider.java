package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.ChiseledBookShelfDataProvider;
import net.minecraft.world.item.ItemStack;

public enum ChiseledBookShelfProvider implements ItemHolderBlockProvider {

    INSTANCE;

    private int lastUpdateId = 0;
    private ItemStack hitItem = ItemStack.EMPTY;

    @Override
    public void init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return;

        lastUpdateId = accessor.getUpdateId();
        hitItem = ItemStack.EMPTY;
        if (!config.getBoolean(Options.BOOK_BOOKSHELF)) return;

        var data = accessor.getData().get(ChiseledBookShelfDataProvider.DATA);
        if (data == null) return;

        var block = ((ChiseledBookShelfBlockAccess) accessor.getBlock());
        var hitSlot = block.wthit_getHitSlot(accessor.getBlockHitResult(), accessor.getBlockState());
        if (hitSlot.isEmpty()) return;

        hitItem = data.items().get(hitSlot.getAsInt());
    }

    @Override
    public ItemStack getItem() {
        return hitItem;
    }

}
