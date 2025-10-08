package mcp.mobius.waila.plugin.extra.provider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemListComponent;
import mcp.mobius.waila.api.component.NamedItemListComponent;
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
    private @Nullable ITooltipComponent lastItemsComponent = null;

    protected ItemProvider() {
        super(ItemData.TYPE, ItemDataImpl.CODEC);
    }

    @Override
    protected void registerAdditions(ICommonRegistrar registrar, int priority) {
        registrar.syncedConfig(ItemData.CONFIG_SYNC_NBT, true, false);
        registrar.localConfig(ItemData.CONFIG_DISPLAY_MODE, ItemData.ItemDisplayMode.DYNAMIC);
        registrar.localConfig(ItemData.CONFIG_MAX_HEIGHT, 3);
        registrar.localConfig(ItemData.CONFIG_SORT_BY_COUNT, true);
        registrar.localConfig(ItemData.CONFIG_GRID_MODE_SCALE, 1f);
    }

    @Override
    protected void appendBody(ITooltip tooltip, IDataReader reader, IPluginConfig config, ResourceLocation objectId) {
        var progress = (ProgressDataImpl) reader.get(ProgressData.TYPE);
        if (progress == null
            || (!progress.hasTick && progress.ratio == 0f)
            || (progress.hasTick && progress.currentTick == 0)
            || ProgressProvider.isProgressOnly(progress, config)) {
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

        var list = stream.toList();
        var maxHeight = config.getInt(ItemData.CONFIG_MAX_HEIGHT);
        var scale = (float) config.getDouble(ItemData.CONFIG_GRID_MODE_SCALE);

        lastItemsComponent = switch (config.<ItemData.ItemDisplayMode>getEnum(ItemData.CONFIG_DISPLAY_MODE)) {
            case DYNAMIC -> list.size() <= maxHeight
                ? new NamedItemListComponent(list, maxHeight)
                : new ItemListComponent(list, maxHeight, scale);
            case GRID -> new ItemListComponent(list, maxHeight, scale);
            case LIST -> new NamedItemListComponent(list, maxHeight);
        };

        tooltip.setLine(ItemData.ID, lastItemsComponent);
    }

    private record ItemWithNbt(Item item, DataComponentPatch tag) {

    }

}
