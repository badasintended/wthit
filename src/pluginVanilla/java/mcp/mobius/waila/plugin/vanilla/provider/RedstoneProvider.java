package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;

public enum RedstoneProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.REDSTONE_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().getValue(BlockStateProperties.POWERED);
            tooltip.setLine(Options.REDSTONE_LEVER, new PairComponent(
                Component.translatable(Tl.Tooltip.STATE),
                Component.translatable(active ? Tl.Tooltip.STATE_ON : Tl.Tooltip.STATE_OFF)));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().getValue(BlockStateProperties.DELAY);
            tooltip.setLine(Options.REDSTONE_REPEATER, new PairComponent(
                Component.translatable(Tl.Tooltip.DELAY),
                Component.literal(String.valueOf(delay))));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            var mode = accessor.getBlockState().getValue(BlockStateProperties.MODE_COMPARATOR);
            tooltip.setLine(Options.REDSTONE_COMPARATOR, new PairComponent(
                Component.translatable(Tl.Tooltip.MODE),
                Component.translatable(mode == ComparatorMode.COMPARE ? Tl.Tooltip.MODE_COMPARATOR : Tl.Tooltip.MODE_SUBTRACTOR)));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_LEVEL) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.setLine(Options.REDSTONE_LEVEL, new PairComponent(
                Component.translatable(Tl.Tooltip.POWER),
                Component.literal(accessor.getBlockState().getValue(BlockStateProperties.POWER).toString())));
        }
    }

}
