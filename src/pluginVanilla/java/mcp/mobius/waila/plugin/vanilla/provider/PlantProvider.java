package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public enum PlantProvider implements IBlockComponentProvider {

    INSTANCE;

    static final ItemStack WHEAT_STACK = new ItemStack(Items.WHEAT);
    static final ItemStack BEETROOT_STACK = new ItemStack(Items.BEETROOT);

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            tooltip.addLine(new PairComponent(
                new TranslatableComponent("tooltip.waila.crop_growth"), new TextComponent(String.format("%.0f%%", growthValue))));
        } else {
            tooltip.addLine(new PairComponent(
                new TranslatableComponent("tooltip.waila.crop_growth"), new TranslatableComponent("tooltip.waila.crop_mature")));
        }
    }

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WHEAT)
            return WHEAT_STACK;

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return BEETROOT_STACK;

        return ItemStack.EMPTY;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropBlock crop) {
                addMaturityTooltip(tooltip, accessor.getBlockState().getValue(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().getValue(BlockStateProperties.AGE_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().getValue(BlockStateProperties.AGE_2) / 2.0F);
            } else if (accessor.getBlock() == Blocks.SWEET_BERRY_BUSH) {
                addMaturityTooltip(tooltip, accessor.getBlockState().getValue(BlockStateProperties.AGE_3) / 3.0F);
            } else if (accessor.getBlock() == Blocks.NETHER_WART) {
                addMaturityTooltip(tooltip, accessor.getBlockState().getValue(BlockStateProperties.AGE_3) / 3.0F);
            }
        }
    }

}
