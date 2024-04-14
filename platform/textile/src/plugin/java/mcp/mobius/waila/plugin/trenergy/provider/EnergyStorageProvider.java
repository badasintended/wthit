package mcp.mobius.waila.plugin.trenergy.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.EnergyData;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public enum EnergyStorageProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockApiCache<EnergyStorage, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(EnergyData.TYPE, res -> {
            if (cache == null || cache.getBlockEntity() != accessor.getTarget()) {
                cache = BlockApiCache.create(EnergyStorage.SIDED, (ServerLevel) accessor.getWorld(), accessor.getTarget().getBlockPos());
            }

            var storage = cache.find(accessor.getTarget().getBlockState(), null);

            if (storage != null) {
                res.add(EnergyData.of(storage.getAmount(), storage.getCapacity()));
            }
        });
    }

}
