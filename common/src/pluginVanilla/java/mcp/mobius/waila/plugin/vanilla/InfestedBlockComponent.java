package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum InfestedBlockComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return config.getBoolean(WailaVanilla.CONFIG_HIDE_SILVERFISH)
            ? ((InfestedBlock) accessor.getBlock()).getHostBlock().defaultBlockState()
            : null;
    }

}
