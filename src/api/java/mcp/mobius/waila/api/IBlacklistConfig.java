package mcp.mobius.waila.api;

import mcp.mobius.waila.impl.Impl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlacklistConfig {

    static IBlacklistConfig get() {
        return Impl.get(IBlacklistConfig.class);
    }

    boolean contains(Block block);

    boolean contains(BlockEntity blockEntity);

    boolean contains(Entity entity);

}
