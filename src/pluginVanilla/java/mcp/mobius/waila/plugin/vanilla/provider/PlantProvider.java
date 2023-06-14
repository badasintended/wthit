package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public enum PlantProvider implements IBlockComponentProvider {

    INSTANCE;

    static final ITooltipComponent WHEAT_STACK = new ItemComponent(Items.WHEAT);
    static final ITooltipComponent BEETROOT_STACK = new ItemComponent(Items.BEETROOT);

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.CROP_GROWTH), Component.literal(String.format("%.0f%%", growthValue))));
        } else {
            tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.CROP_GROWTH), Component.translatable(Tl.Tooltip.CROP_MATURE)));
        }
    }

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WHEAT)
            return WHEAT_STACK;

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return BEETROOT_STACK;

        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.CROP_PROGRESS)) {
            if (accessor.getBlock() instanceof CropBlock crop) {
                addMaturityTooltip(tooltip, crop.getAge(accessor.getBlockState()) / (float) crop.getMaxAge());
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
