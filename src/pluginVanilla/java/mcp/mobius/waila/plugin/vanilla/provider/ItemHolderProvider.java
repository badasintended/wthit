package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.function.Function;

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
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemHolderProvider implements IEntityComponentProvider {

    ITEM_FRAME(accessor -> accessor.<ItemFrame>getEntity().getItem()),
    OMINOUS_ITEM_SPAWNER(accessor -> accessor.<OminousItemSpawner>getEntity().getItem());

    final Function<IEntityAccessor, ItemStack> getter;

    ItemHolderProvider(Function<IEntityAccessor, ItemStack> getter) {
        this.getter = getter;
    }

    @Nullable
    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        var stack = getter.apply(accessor);
        return stack.isEmpty() ? null : new ItemComponent(stack);
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = getter.apply(accessor);

        if (!stack.isEmpty()) {
            var formatter = IWailaConfig.get().getFormatter();
            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(stack.getHoverName().getString()));

            if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
                tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(stack.getItem())));
            }
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = getter.apply(accessor);
        ItemEntityProvider.appendBookProperties(tooltip, stack, config);
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = getter.apply(accessor);

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME) && !stack.isEmpty()) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(stack).getName()));
        }
    }

}
