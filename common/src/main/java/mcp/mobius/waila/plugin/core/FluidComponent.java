package mcp.mobius.waila.plugin.core;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum FluidComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WATER)
            return new ItemStack(Items.WATER_BUCKET);
        else if (accessor.getBlock() == Blocks.LAVA)
            return new ItemStack(Items.LAVA_BUCKET);

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHead(List<Component> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(accessor.getFluidNameFormat(), block.getName().getString())));
        if (config.get(WailaConstants.CONFIG_SHOW_REGISTRY))
            ((ITaggableList<ResourceLocation, Component>) tooltip).setTag(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(String.format(accessor.getRegistryNameFormat(), Registry.BLOCK.getKey(block))));
    }

}
