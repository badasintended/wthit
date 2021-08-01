package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;

public enum FallingBlockComponent implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public ItemStack getDisplayItem(IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        return new ItemStack(entity.getBlockState().getBlock().asItem());
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        tooltip.set(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(accessor.getEntityNameFormat(), entity.getBlockState().getBlock().getName().getContents())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            tooltip.set(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(Registry.ENTITY_TYPE.getKey(accessor.getEntity().getType()).toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

}
