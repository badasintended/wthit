package mcp.mobius.waila.plugin.textile.provider;

import java.util.HashSet;
import java.util.Set;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.fabric.FabricFluidData;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public enum FluidStorageProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockApiCache<Storage<FluidVariant>, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.TYPE, res -> {
            if (cache == null || cache.getBlockEntity() != accessor.getTarget()) {
                cache = BlockApiCache.create(FluidStorage.SIDED, (ServerLevel) accessor.getWorld(), accessor.getTarget().getBlockPos());
            }

            var storage = cache.find(accessor.getTarget().getBlockState(), null);

            if (storage instanceof SingleSlotStorage<FluidVariant> single) {
                var fluidData = FabricFluidData.of();
                addFluid(fluidData, single);
                res.add(fluidData);
            } else if (storage instanceof SlottedStorage<FluidVariant> slotted) {
                var size = slotted.getSlotCount();
                var fluidData = FabricFluidData.of(size);

                for (var i = 0; i < size; i++) {
                    var slot = slotted.getSlot(i);
                    addFluid(fluidData, slot);
                }

                res.add(fluidData);
            } else if (storage != null) {
                Set<StorageView<FluidVariant>> uniqueViews = new HashSet<>();
                var fluidData = FabricFluidData.of();

                for (var view : storage) {
                    addFluid(uniqueViews, fluidData, view);
                }

                res.add(fluidData);
            }
        });
    }

    private void addFluid(Set<StorageView<FluidVariant>> uniqueViews, FluidData.PlatformDependant<FluidVariant> fluidData, StorageView<FluidVariant> view) {
        var uniqueView = view.getUnderlyingView();
        if (uniqueViews.add(uniqueView)) addFluid(fluidData, view);
    }

    private void addFluid(FluidData.PlatformDependant<FluidVariant> fluidData, StorageView<FluidVariant> view) {
        if (view.isResourceBlank()) return;
        fluidData.add(view.getResource(), view.getAmount(), view.getCapacity());
    }

}
