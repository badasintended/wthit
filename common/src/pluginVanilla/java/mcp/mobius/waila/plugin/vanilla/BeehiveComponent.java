package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum BeehiveComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaVanilla.CONFIG_HONEY_LEVEL)) {
            BlockState state = accessor.getBlockState();
            tooltip.add(new TranslatableComponent("tooltip.waila.honey_level", state.getValue(BeehiveBlock.HONEY_LEVEL)));
        }
    }

}
