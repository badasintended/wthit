package mcp.mobius.waila.plugin.neo.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.neo.NeoFluidData;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;

public enum FluidCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.TYPE, res -> {
            var world = (ServerLevel) accessor.getWorld();
            var target = accessor.getTarget();
            var pos = target.getBlockPos();

            if (cache == null || (cache.level() != world && !cache.pos().equals(pos))) {
                cache = BlockCapabilityCache.create(Capabilities.Fluid.BLOCK, world, pos, null);
            }

            var handler = cache.getCapability();
            if (handler == null) return;

            var size = handler.size();
            var fluidData = NeoFluidData.of(size);

            for (var i = 0; i < size; i++) {
                var resource = handler.getResource(i);
                fluidData.add(resource, handler.getAmountAsLong(i), handler.getCapacityAsLong(i, resource));
            }

            res.add(fluidData);
        });
    }

}
