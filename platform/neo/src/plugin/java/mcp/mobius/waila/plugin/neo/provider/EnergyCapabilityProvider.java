package mcp.mobius.waila.plugin.neo.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.EnergyData;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;

public enum EnergyCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockCapabilityCache<EnergyHandler, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(EnergyData.TYPE, res -> {
            var world = (ServerLevel) accessor.getWorld();
            var target = accessor.getTarget();
            var pos = target.getBlockPos();

            if (cache == null || (cache.level() != world && !cache.pos().equals(pos))) {
                cache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, world, pos, null);
            }

            var storage = cache.getCapability();
            if (storage == null) return;

            res.add(EnergyData.of(storage.getAmountAsLong(), storage.getCapacityAsLong()));
        });
    }

}
