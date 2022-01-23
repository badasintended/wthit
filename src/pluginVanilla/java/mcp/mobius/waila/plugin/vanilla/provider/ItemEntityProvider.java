package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemEntityProvider implements IEntityComponentProvider {

    INSTANCE;

    @Nullable
    @Override
    public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        return !config.getBoolean(Options.ITEM_ENTITY) ? EMPTY_ENTITY : null;
    }

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        return accessor.<ItemEntity>getEntity().getItem();
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IWailaConfig.Formatting formatting = IWailaConfig.get().getFormatting();

        ItemStack stack = accessor.<ItemEntity>getEntity().getItem();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, new TextComponent(formatting.formatEntityName(stack.getHoverName().getString())));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY))
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(formatting.formatRegistryName(Registry.ITEM.getKey(stack.getItem()))));
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            String mod = IModInfo.get(accessor.<ItemEntity>getEntity().getItem()).getName();
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, new TextComponent(IWailaConfig.get().getFormatting().formatModName(mod)));
        }
    }
}
