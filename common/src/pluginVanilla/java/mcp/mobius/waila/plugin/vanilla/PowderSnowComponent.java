package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public enum PowderSnowComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @Nullable BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return config.get(WailaVanilla.CONFIG_HIDE_POWDER_SNOW)
            ? Blocks.SNOW_BLOCK.defaultBlockState()
            : null;
    }

}
