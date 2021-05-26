package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum PlantComponent implements IBlockComponentProvider {

    INSTANCE;

    static final ItemStack WHEAT_STACK = new ItemStack(Items.WHEAT);
    static final ItemStack BEETROOT_STACK = new ItemStack(Items.BEETROOT);

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WHEAT)
            return WHEAT_STACK;

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return BEETROOT_STACK;

        return ItemStack.EMPTY;
    }

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropBlock crop) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(crop.getAgeProperty()) / (float) crop.getMaxAge());
            } else if (accessor.getBlock() == Blocks.MELON_STEM || accessor.getBlock() == Blocks.PUMPKIN_STEM) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_7) / 7F);
            } else if (accessor.getBlock() == Blocks.COCOA) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_2) / 2.0F);
            } else if (accessor.getBlock() == Blocks.SWEET_BERRY_BUSH) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
            } else if (accessor.getBlock() == Blocks.NETHER_WART) {
                addMaturityTooltip(tooltip, accessor.getBlockState().get(Properties.AGE_3) / 3.0F);
            }
        }
    }

    private static void addMaturityTooltip(List<Text> tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F)
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", String.format("%.0f%%", growthValue)));
        else
            tooltip.add(new TranslatableText("tooltip.waila.crop_growth", new TranslatableText("tooltip.waila.crop_mature")));
    }

}
