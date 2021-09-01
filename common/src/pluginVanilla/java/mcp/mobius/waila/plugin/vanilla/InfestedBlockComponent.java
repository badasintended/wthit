package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum InfestedBlockComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return config.getBoolean(Options.OVERRIDE_INFESTED)
            ? ((InfestedBlock) accessor.getBlock()).getHostBlock().defaultBlockState()
            : null;
    }

}
