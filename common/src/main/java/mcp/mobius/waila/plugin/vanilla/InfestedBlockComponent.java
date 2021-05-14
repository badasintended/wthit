package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;

public enum InfestedBlockComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return config.get(WailaVanilla.CONFIG_HIDE_SILVERFISH)
            ? ((InfestedBlock) accessor.getBlock()).getRegularBlock().getDefaultState()
            : null;
    }

}
