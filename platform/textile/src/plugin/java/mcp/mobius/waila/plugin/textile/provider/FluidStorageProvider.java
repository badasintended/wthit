package mcp.mobius.waila.plugin.textile.provider;

import java.util.HashSet;
import java.util.Set;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
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

@SuppressWarnings("UnstableApiUsage")
public enum FluidStorageProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockApiCache<Storage<FluidVariant>, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.class, res -> {
            if (cache == null || cache.getBlockEntity() != accessor.getTarget()) {
                cache = BlockApiCache.create(FluidStorage.SIDED, (ServerLevel) accessor.getWorld(), accessor.getTarget().getBlockPos());
            }

            Storage<FluidVariant> storage = cache.find(accessor.getTarget().getBlockState(), null);

            if (storage instanceof SingleSlotStorage<FluidVariant> single) {
                FluidData fluidData = FluidData.of(1);
                addFluid(fluidData, single);
                res.add(fluidData);
            } else if (storage instanceof SlottedStorage<FluidVariant> slotted) {
                int size = slotted.getSlotCount();
                FluidData fluidData = FluidData.of(size);

                for (int i = 0; i < size; i++) {
                    SingleSlotStorage<FluidVariant> slot = slotted.getSlot(i);
                    addFluid(fluidData, slot);
                }

                res.add(fluidData);
            } else if (storage != null) {
                Set<StorageView<FluidVariant>> uniqueViews = new HashSet<>();
                FluidData fluidData = FluidData.of();

                for (StorageView<FluidVariant> view : storage) {
                    addFluid(uniqueViews, fluidData, view);
                }

                res.add(fluidData);
            }
        });
    }

    private void addFluid(Set<StorageView<FluidVariant>> uniqueViews, FluidData fluidData, StorageView<FluidVariant> view) {
        StorageView<FluidVariant> uniqueView = view.getUnderlyingView();
        if (uniqueViews.add(uniqueView)) addFluid(fluidData, view);
    }

    private void addFluid(FluidData fluidData, StorageView<FluidVariant> view) {
        if (view.isResourceBlank()) return;
        FluidVariant variant = view.getResource();
        fluidData.add(variant.getFluid(), variant.getNbt(), view.getAmount() / 81.0, view.getCapacity() / 81.0);
    }

}
