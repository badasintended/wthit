package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.LecternDataProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;

public enum LecternProvider implements ItemShowcaseBlockProvider {

    INSTANCE;

    private int lastUpdateId = 0;
    private ItemStack book = ItemStack.EMPTY;

    public ItemStack init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return book;

        lastUpdateId = accessor.getUpdateId();
        book = ItemStack.EMPTY;
        if (!config.getBoolean(Options.BOOK_LECTERN)) return book;

        var data = accessor.getData().get(LecternDataProvider.DATA);
        if (data == null) return book;

        var hit = accessor.getBlockHitResult();
        var hitDir = hit.getDirection();
        var yOffset = hit.getLocation().y() - hit.getBlockPos().getY();
        if (yOffset < 0.875) return book;

        var direction = accessor.getBlockState().getValue(LecternBlock.FACING);
        if (hitDir != direction && hitDir != Direction.UP) return book;

        book = data.book();
        return book;
    }

}
