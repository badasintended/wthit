package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemFrameProvider implements IEntityComponentProvider {

    INSTANCE;

    @Nullable
    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        ItemStack stack = accessor.<ItemFrame>getEntity().getItem();
        return stack.isEmpty() ? null : new ItemComponent(stack);
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ItemStack stack = accessor.<ItemFrame>getEntity().getItem();

        if (!stack.isEmpty()) {
            IWailaConfig.Formatter formatter = IWailaConfig.get().getFormatter();
            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(stack.getHoverName().getString()));

            if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
                tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(stack.getItem())));
            }
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        ItemStack stack = accessor.<ItemFrame>getEntity().getItem();

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME) && !stack.isEmpty()) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(stack).getName()));
        }
    }

}
