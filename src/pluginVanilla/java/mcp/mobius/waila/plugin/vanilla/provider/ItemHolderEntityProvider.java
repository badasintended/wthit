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
import org.jetbrains.annotations.Nullable;

<<<<<<<< HEAD:src/pluginVanilla/java/mcp/mobius/waila/plugin/vanilla/provider/ItemFrameProvider.java
public enum ItemFrameProvider implements IEntityComponentProvider {

    INSTANCE;
========
public enum ItemHolderEntityProvider implements IEntityComponentProvider {

    ITEM_FRAME(accessor -> accessor.<ItemFrame>getEntity().getItem()),
    OMINOUS_ITEM_SPAWNER(accessor -> accessor.<OminousItemSpawner>getEntity().getItem());

    final Function<IEntityAccessor, ItemStack> getter;

    ItemHolderEntityProvider(Function<IEntityAccessor, ItemStack> getter) {
        this.getter = getter;
    }
>>>>>>>> c8e50b9e (show book page count):src/pluginVanilla/java/mcp/mobius/waila/plugin/vanilla/provider/ItemHolderEntityProvider.java

    @Nullable
    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        var stack = accessor.<ItemFrame>getEntity().getItem();
        return stack.isEmpty() ? null : new ItemComponent(stack);
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = accessor.<ItemFrame>getEntity().getItem();

        if (!stack.isEmpty()) {
            var formatter = IWailaConfig.get().getFormatter();
            tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(stack.getHoverName()));

            if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
                tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(stack.getItem())));
            }
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
<<<<<<<< HEAD:src/pluginVanilla/java/mcp/mobius/waila/plugin/vanilla/provider/ItemFrameProvider.java
        var stack = accessor.<ItemFrame>getEntity().getItem();
        ItemEntityProvider.appendBookProperties(tooltip, stack, config);
========
        var stack = getter.apply(accessor);
        ItemEntityProvider.appendBookProperties(tooltip, stack, config, true);
>>>>>>>> c8e50b9e (show book page count):src/pluginVanilla/java/mcp/mobius/waila/plugin/vanilla/provider/ItemHolderEntityProvider.java
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = accessor.<ItemFrame>getEntity().getItem();

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME) && !stack.isEmpty()) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(stack).getName()));
        }
    }

}
