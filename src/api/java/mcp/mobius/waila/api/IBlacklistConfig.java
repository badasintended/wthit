package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlacklistConfig {

    static IBlacklistConfig get() {
        return IApiService.INSTANCE.getBlacklistConfig();
    }

    boolean contains(Block block);

    boolean contains(BlockEntity blockEntity);

    boolean contains(Entity entity);

}
