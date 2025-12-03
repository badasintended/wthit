package mcp.mobius.waila.plugin.core.event;

import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.world.item.ItemStack;

public enum CoreEventListener implements IEventListener {

    INSTANCE;

    @Override
    public String getHoveredItemModName(ItemStack stack, IPluginConfig config) {
        return IModInfo.get(stack).getName();
    }

}
