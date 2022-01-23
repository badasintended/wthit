package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public enum PowderSnowProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @Nullable BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        return config.getBoolean(Options.OVERRIDE_POWDER_SNOW)
            ? Blocks.SNOW_BLOCK.defaultBlockState()
            : null;
    }

}
