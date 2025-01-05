package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public enum PlantProvider implements IBlockComponentProvider {

    INSTANCE;

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        var line = tooltip.setLine(Options.PLANT_CROP_PROGRESS);
        growthValue *= 100.0F;
        if (growthValue < 100.0F) {
            line.with(new PairComponent(
                Component.translatable(Tl.Tooltip.CROP_GROWTH), Component.literal(String.format("%.0f%%", growthValue))));
        } else {
            line.with(new PairComponent(
                Component.translatable(Tl.Tooltip.CROP_GROWTH), Component.translatable(Tl.Tooltip.CROP_MATURE)));
        }
    }

    private static void addGrowableTooltip(ITooltip tooltip, ResourceLocation tag, String translationKey, boolean growable) {
        tooltip.setLine(tag, new PairComponent(Component.translatable(translationKey),
            growable ? Component.translatable(Tl.Tooltip.TRUE) : Component.translatable(Tl.Tooltip.FALSE)));
    }

    private static void addCropGrowableTooltip(ITooltip tooltip, IBlockAccessor accessor) {
        var lightLevel = accessor.getWorld().getRawBrightness(accessor.getPosition(), 0);
        addGrowableTooltip(tooltip, Options.PLANT_CROP_GROWABLE, Tl.Tooltip.CROP_GROWABLE, lightLevel >= 9);
    }

    private static void addTreeGrowableTooltip(ITooltip tooltip, IBlockAccessor accessor) {
        var lightLevel = accessor.getWorld().getRawBrightness(accessor.getPosition(), 0);
        var growable = lightLevel >= 9;
        if (accessor.getBlock() instanceof MangrovePropaguleBlock
            && accessor.getBlockState().getValue(MangrovePropaguleBlock.HANGING)) {
            growable = false;
        }

        addGrowableTooltip(tooltip, Options.PLANT_TREE_GROWABLE, Tl.Tooltip.TREE_GROWABLE, growable);
    }

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.WHEAT)
            return new ItemComponent(Items.WHEAT);

        if (accessor.getBlock() == Blocks.BEETROOTS)
            return new ItemComponent(Items.BEETROOT);

        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PLANT_CROP_PROGRESS)) {
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

        if (config.getBoolean(Options.PLANT_CROP_GROWABLE)) {
            if ((accessor.getBlock() instanceof CropBlock || accessor.getBlock() instanceof StemBlock)
                && IModInfo.get(accessor.getBlock()).getId().equals("minecraft")) {
                addCropGrowableTooltip(tooltip, accessor);
            }
        }

        if (config.getBoolean(Options.PLANT_TREE_GROWABLE)) {
            if (accessor.getBlock() instanceof SaplingBlock && IModInfo.get(accessor.getBlock()).getId().equals("minecraft")) {
                addTreeGrowableTooltip(tooltip, accessor);
            }
        }
    }

}
