package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.SelectableSlotContainerDataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.SelectableSlotContainer;

public enum SelectableSlotContainerProvider implements ItemShowcaseBlockProvider {

    SHELF(Options.SHELF_ITEMS),
    BOOKSHELF(Options.BOOK_BOOKSHELF);

    private final Identifier option;

    private int lastUpdateId = 0;
    private ItemStack hitItem = ItemStack.EMPTY;

    SelectableSlotContainerProvider(Identifier option) {
        this.option = option;
    }

    @Override
    public ItemStack init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return hitItem;

        lastUpdateId = accessor.getUpdateId();
        hitItem = ItemStack.EMPTY;
        if (!config.getBoolean(option)) return hitItem;

        var data = accessor.getData().get(SelectableSlotContainerDataProvider.DATA);
        if (data == null) return hitItem;

        var block = ((SelectableSlotContainer) accessor.getBlock());
        var hitSlot = block.getHitSlot(accessor.getBlockHitResult(), accessor.getBlockState().getValue(ChiseledBookShelfBlock.FACING));
        if (hitSlot.isEmpty()) return hitItem;

        hitItem = data.items().get(hitSlot.getAsInt());
        return hitItem;
    }

}
