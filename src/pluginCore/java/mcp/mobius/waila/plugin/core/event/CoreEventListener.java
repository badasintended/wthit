package mcp.mobius.waila.plugin.core.event;

import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum CoreEventListener implements IEventListener {

    INSTANCE;

    @Nullable
    @Override
    public String getHoveredItemModName(ItemStack stack, IPluginConfig config) {
        return IModInfo.get(stack).getName();
    }

}
