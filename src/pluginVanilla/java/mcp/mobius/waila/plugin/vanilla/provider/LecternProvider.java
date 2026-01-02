package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.LecternDataProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import org.jetbrains.annotations.Nullable;

public enum LecternProvider implements ItemHolderBlockProvider {

    INSTANCE;

    private int lastUpdateId = 0;
    private LecternDataProvider.@Nullable Data cachedData;

    public void init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return;

        lastUpdateId = accessor.getUpdateId();
        cachedData = null;
        if (!config.getBoolean(Options.BOOK_LECTERN)) return;

        var data = accessor.getData().get(LecternDataProvider.Data.class);
        if (data == null) return;

        var hit = accessor.getBlockHitResult();
        var hitDir = hit.getDirection();
        var yOffset = hit.getLocation().y() - hit.getBlockPos().getY();
        if (yOffset < 0.875) return;

        var direction = accessor.getBlockState().getValue(LecternBlock.FACING);
        if (hitDir != direction && hitDir != Direction.UP) return;

        cachedData = data;
    }

    @Override
    public ItemStack getItem() {
        return cachedData == null ? ItemStack.EMPTY : cachedData.book();
    }

    @Override
    public boolean showBookPages() {
        return false;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (cachedData == null) return;
        ItemHolderBlockProvider.super.appendBody(tooltip, accessor, config);

        tooltip.setLine(ItemEntityProvider.PAGES, Component.translatable(Tl.Tooltip.Lectern.PAGE, cachedData.page(), cachedData.pageCount()));
    }

}
