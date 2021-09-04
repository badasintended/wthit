package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;

public enum RedstoneComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.REDSTONE_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().getValue(BlockStateProperties.POWERED);
            tooltip.add(new TranslatableComponent("tooltip.waila.state", new TranslatableComponent("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().getValue(BlockStateProperties.DELAY);
            tooltip.add(new TranslatableComponent("tooltip.waila.delay", delay));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().getValue(BlockStateProperties.MODE_COMPARATOR);
            tooltip.add(new TranslatableComponent("tooltip.waila.mode", new TranslatableComponent("tooltip.waila.mode_" + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.getBoolean(Options.REDSTONE_LEVEL) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslatableComponent("tooltip.waila.power", accessor.getBlockState().getValue(BlockStateProperties.POWER)));
        }
    }

}
