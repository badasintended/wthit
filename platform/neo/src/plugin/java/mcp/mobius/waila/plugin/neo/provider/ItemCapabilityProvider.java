package mcp.mobius.waila.plugin.neo.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public enum ItemCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockCapabilityCache<IItemHandler, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.class, res -> {
            var world = (ServerLevel) accessor.getWorld();
            var target = accessor.getTarget();
            var pos = target.getBlockPos();

            if (cache == null || (cache.level() != world && !cache.pos().equals(pos))) {
                //noinspection DataFlowIssue
                cache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, world, pos, null);
            }

            var handler = cache.getCapability();
            if (handler == null) return;

            res.add(ItemData.of(config).getter(handler::getStackInSlot, handler.getSlots()));
        });
    }

}
