package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
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
    public void appendHead(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = (FallingBlockEntity) accessor.getEntity();
        ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(accessor.getEntityNameFormat(), entity.getBlockState().getBlock().getName().getContents())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(Registry.ENTITY_TYPE.getKey(accessor.getEntity().getType()).toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

}
