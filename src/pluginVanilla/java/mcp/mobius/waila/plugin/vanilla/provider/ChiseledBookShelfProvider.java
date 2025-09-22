package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.ChiseledBookShelfDataProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import org.jetbrains.annotations.Nullable;

public enum ChiseledBookShelfProvider implements IBlockComponentProvider {

    INSTANCE;

    private int lastUpdateId = 0;
    private ItemStack hitItem = ItemStack.EMPTY;

    private void init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return;

        lastUpdateId = accessor.getUpdateId();
        hitItem = ItemStack.EMPTY;
        if (!config.getBoolean(Options.BOOK_BOOKSHELF)) return;

        var data = accessor.getData().get(ChiseledBookShelfDataProvider.DATA);
        if (data == null) return;

        var block = ((ChiseledBookShelfBlock) accessor.getBlock());
        var hitSlot = block.getHitSlot(accessor.getBlockHitResult(), accessor.getBlockState().getValue(ChiseledBookShelfBlock.FACING));
        if (hitSlot.isEmpty()) return;

        hitItem = data.items().get(hitSlot.getAsInt());
    }

    @Override
    public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return null;

        return new ItemComponent(hitItem);
    }

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;

        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(hitItem.getHoverName().getString()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(hitItem.getItem())));
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;

        ItemEntityProvider.appendBookProperties(tooltip, hitItem, config);
    }

    @Override
    public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;
        if (!config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) return;

        tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(hitItem).getName()));
    }

}
