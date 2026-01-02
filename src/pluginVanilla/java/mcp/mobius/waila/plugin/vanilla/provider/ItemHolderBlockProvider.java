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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface ItemHolderBlockProvider extends IBlockComponentProvider {

    void init(IBlockAccessor accessor, IPluginConfig config);

    ItemStack getItem();

    default boolean showBookPages() {
        return true;
    }

    @Override
    default @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        var item = getItem();
        if (item.isEmpty()) return null;
        return new ItemComponent(item);
    }

    @Override
    default void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        var item = getItem();
        if (item.isEmpty()) return;

        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(item.getHoverName()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(item.getItem())));
        }
    }

    @Override
    default void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        var item = getItem();
        if (item.isEmpty()) return;

        ItemEntityProvider.appendBookProperties(tooltip, item, config, showBookPages());
    }

    @Override
    default void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        var item = getItem();
        if (item.isEmpty()) return;
        if (!config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) return;

        tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(item).getName()));
    }

}
