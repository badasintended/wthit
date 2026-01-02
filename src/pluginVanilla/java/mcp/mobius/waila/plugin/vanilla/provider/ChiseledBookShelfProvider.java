package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.ChiseledBookShelfDataProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

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

        var data = accessor.getData().get(ChiseledBookShelfDataProvider.Data.class);
        if (data == null) return;

        var blockstate = accessor.getBlockState();
        var facing = blockstate.getValue(HorizontalDirectionalBlock.FACING);
        var relativeHit = ChiseledBookShelfBlockAccess.wthit_getRelativeHitCoordinatesForBlockFace(accessor.getBlockHitResult(), facing);
        if (relativeHit.isEmpty()) return;

        var hitSlot = ChiseledBookShelfBlockAccess.wthit_getHitSlot(relativeHit.get());
        hitItem = data.items().get(hitSlot);
    }

    @Override
    public ItemStack getItem() {
        return hitItem;
    }

}
