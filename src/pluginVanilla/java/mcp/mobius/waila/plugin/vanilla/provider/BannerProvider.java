package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public enum BannerProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getData().raw();

        BannerBlockEntity be = accessor.getBlockEntity();
        if (be == null) return;

        var nameComponent = be.components().get(DataComponents.ITEM_NAME);
        if (nameComponent == null) return;

        var name = nameComponent.getString();
        if (data.contains("customName")) {
            name = data.getString("customName") + " (" + name + ")";
        }

        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, IWailaConfig.get().getFormatter().blockName(name));
    }
}
