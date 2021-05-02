package mcp.mobius.waila.plugin.core;

import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.config.WailaConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
    public void appendHead(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        WailaConfig.ConfigFormatting formatting = Waila.config.get().getFormatting();
        ((ITaggableList<Identifier, Text>) tooltip).setTag(BlockComponent.OBJECT_NAME_TAG, new LiteralText(String.format(formatting.getFluidName(), block.getName().getString())));
        if (MinecraftClient.getInstance().options.advancedItemTooltips)
            ((ITaggableList<Identifier, Text>) tooltip).setTag(BlockComponent.REGISTRY_NAME_TAG, new LiteralText(String.format(formatting.getRegistryName(), Registry.BLOCK.getId(block))));
    }

}
