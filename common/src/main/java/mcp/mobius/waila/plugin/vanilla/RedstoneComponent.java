package mcp.mobius.waila.plugin.vanilla;

import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum RedstoneComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.get(WailaVanilla.CONFIG_LEVER) && accessor.getBlock() instanceof LeverBlock) {
            boolean active = accessor.getBlockState().get(Properties.POWERED);
            tooltip.add(new TranslatableText("tooltip.waila.state", new TranslatableText("tooltip.waila.state_" + (active ? "on" : "off"))));
            return;
        }

        if (config.get(WailaVanilla.CONFIG_REPEATER) && accessor.getBlock() == Blocks.REPEATER) {
            int delay = accessor.getBlockState().get(Properties.DELAY);
            tooltip.add(new TranslatableText("tooltip.waila.delay", delay));
            return;
        }

        if (config.get(WailaVanilla.CONFIG_COMPARATOR) && accessor.getBlock() == Blocks.COMPARATOR) {
            ComparatorMode mode = accessor.getBlockState().get(Properties.COMPARATOR_MODE);
            tooltip.add(new TranslatableText("tooltip.waila.mode", new TranslatableText("tooltip.waila.mode_" + (mode == ComparatorMode.COMPARE ? "comparator" : "subtractor"))));
            return;
        }

        if (config.get(WailaVanilla.CONFIG_REDSTONE) && accessor.getBlock() == Blocks.REDSTONE_WIRE) {
            tooltip.add(new TranslatableText("tooltip.waila.power", accessor.getBlockState().get(Properties.POWER)));
        }
    }

}
