package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import static net.minecraft.block.ChestBlock.CHEST_TYPE;
import static net.minecraft.block.ChestBlock.FACING;
import static net.minecraft.block.ChestBlock.WATERLOGGED;

public enum TrappedChestComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        return Blocks.CHEST.getDefaultState()
            .with(FACING, state.get(FACING))
            .with(CHEST_TYPE, state.get(CHEST_TYPE))
            .with(WATERLOGGED, state.get(WATERLOGGED));
    }

}
