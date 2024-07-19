package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.ChestBlock.FACING;
import static net.minecraft.world.level.block.ChestBlock.TYPE;
import static net.minecraft.world.level.block.ChestBlock.WATERLOGGED;

public enum TrappedChestProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public @Nullable BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.OVERRIDE_TRAPPED_CHEST)) {
            var state = accessor.getBlockState();
            return Blocks.CHEST.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(TYPE, state.getValue(TYPE))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
        }
        return null;
    }

}
