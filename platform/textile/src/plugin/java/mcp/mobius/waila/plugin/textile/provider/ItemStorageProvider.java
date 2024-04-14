package mcp.mobius.waila.plugin.textile.provider;

import java.util.HashSet;
import java.util.Set;

import com.google.common.primitives.Ints;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public enum ItemStorageProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Nullable
    private BlockApiCache<Storage<ItemVariant>, @Nullable Direction> cache;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.TYPE, res -> {
            if (cache == null || cache.getBlockEntity() != accessor.getTarget()) {
                cache = BlockApiCache.create(ItemStorage.SIDED, (ServerLevel) accessor.getWorld(), accessor.getTarget().getBlockPos());
            }

            var storage = cache.find(accessor.getTarget().getBlockState(), null);

            if (storage instanceof SingleSlotStorage<ItemVariant> single) {
                var itemData = ItemData.of(config);
                addItem(itemData, single);
                res.add(itemData);
            } else if (storage instanceof SlottedStorage<ItemVariant> slotted) {
                var size = slotted.getSlotCount();
                var itemData = ItemData.of(config);
                itemData.ensureSpace(size);

                for (var i = 0; i < size; i++) {
                    var slot = slotted.getSlot(i);
                    addItem(itemData, slot);
                }

                res.add(itemData);
            } else if (storage != null) {
                Set<StorageView<ItemVariant>> uniqueViews = new HashSet<>();
                var itemData = ItemData.of(config);

                for (var view : storage) {
                    addItem(uniqueViews, itemData, view);
                }

                res.add(itemData);
            }
        });
    }

    private void addItem(Set<StorageView<ItemVariant>> uniqueViews, ItemData itemData, StorageView<ItemVariant> view) {
        var uniqueView = view.getUnderlyingView();
        if (uniqueViews.add(uniqueView)) addItem(itemData, view);
    }

    private void addItem(ItemData itemData, StorageView<ItemVariant> view) {
        if (view.isResourceBlank()) return;
        itemData.add(view.getResource().toStack(Ints.saturatedCast(view.getAmount())));
    }

}
