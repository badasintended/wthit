package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemEntityComponent implements IEntityComponentProvider {

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
        String name = accessor.<ItemEntity>getEntity().getItem().getHoverName().getString();
        tooltip.set(WailaConstants.OBJECT_NAME_TAG, new TextComponent(IWailaConfig.get().getFormatting().formatEntityName(name)));
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        String mod = IModInfo.get(accessor.<ItemEntity>getEntity().getItem()).getName();
        tooltip.set(WailaConstants.MOD_NAME_TAG, new TextComponent(IWailaConfig.get().getFormatting().formatModName(mod)));
    }
}
