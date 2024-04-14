package mcp.mobius.waila.plugin.extra.provider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ItemListComponent;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.plugin.extra.data.ItemDataImpl;
import mcp.mobius.waila.plugin.extra.data.ProgressDataImpl;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemProvider extends DataProvider<ItemData, ItemDataImpl> {

    public static final ItemProvider INSTANCE = new ItemProvider();

    private @Nullable ItemData lastData = null;
    private @Nullable ItemListComponent lastItemsComponent = null;

    protected ItemProvider() {
        super(ItemData.TYPE, ItemDataImpl.CODEC);
    }

    @Override
    protected void registerAdditions(IRegistrar registrar, int priority) {
        registrar.addSyncedConfig(ItemData.CONFIG_SYNC_NBT, true, false);
        registrar.addConfig(ItemData.CONFIG_MAX_HEIGHT, 3);
        registrar.addConfig(ItemData.CONFIG_SORT_BY_COUNT, true);
    }

    @Override
    protected void appendBody(ITooltip tooltip, IDataReader reader, IPluginConfig config, ResourceLocation objectId) {
        var progress = (ProgressDataImpl) reader.get(ProgressData.TYPE);
        if (progress == null || progress.ratio() == 0f) {
            super.appendBody(tooltip, reader, config, objectId);
        }
    }

    @Override
    protected void appendBody(ITooltip tooltip, ItemDataImpl data, IPluginConfig config, ResourceLocation objectId) {
        if (data == lastData) {
            if (lastItemsComponent != null) tooltip.setLine(ItemData.ID, lastItemsComponent);
            return;
        }

        lastData = data;
        lastItemsComponent = null;

        Map<Object, ItemStack> merged = new LinkedHashMap<>();
        Map<Item, Set<DataComponentPatch>> unique = new HashMap<>();

        for (var stack : data.items()) {
            if (stack.isEmpty()) continue;

            var item = stack.getItem();
            var count = stack.getCount();

            if (!data.syncNbt()) {
                if (unique.put(item, Set.of()) != null) {
                    merged.get(item).grow(count);
                } else {
                    merged.put(item, stack.copy());
                }
            } else {
                var nbt = stack.getComponentsPatch();

                if (unique.computeIfAbsent(item, i -> new HashSet<>()).add(nbt)) {
                    merged.put(new ItemWithNbt(item, nbt), stack.copy());
                } else {
                    merged.get(new ItemWithNbt(item, nbt)).grow(count);
                }
            }
        }

        if (merged.isEmpty()) return;

        var stream = merged.values().stream();
        if (config.getBoolean(ItemData.CONFIG_SORT_BY_COUNT)) {
            stream = stream.sorted(Comparator.comparingInt(ItemStack::getCount).reversed());
        }

        tooltip.setLine(ItemData.ID, lastItemsComponent = new ItemListComponent(stream.toList(), config.getInt(ItemData.CONFIG_MAX_HEIGHT)));
    }

    private record ItemWithNbt(Item item, DataComponentPatch tag) {

    }

}
