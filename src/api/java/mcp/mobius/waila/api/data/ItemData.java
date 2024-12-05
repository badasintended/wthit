package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Adds item information to an object.
 */
@ApiStatus.NonExtendable
public abstract class ItemData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("item");
    public static final Type<ItemData> TYPE = IData.createType(ID);

    public static final ResourceLocation CONFIG_SYNC_NBT = BuiltinDataUtil.rl("item.nbt");
    public static final ResourceLocation CONFIG_DISPLAY_MODE = BuiltinDataUtil.rl("item.display_mode");
    public static final ResourceLocation CONFIG_MAX_HEIGHT = BuiltinDataUtil.rl("item.max_height");
    public static final ResourceLocation CONFIG_SORT_BY_COUNT = BuiltinDataUtil.rl("item.sort_by_count");
    public static final ResourceLocation CONFIG_GRID_MODE_SCALE = BuiltinDataUtil.rl("item.grid_mode_scale");

    public enum ItemDisplayMode {
        GRID, LIST, DYNAMIC
    }

    /**
     * Creates an item data based from plugin config.
     */
    public static ItemData of(IPluginConfig config) {
        return IExtraService.INSTANCE.createItemData(config);
    }

    /**
     * Adds items from vanilla container.
     */
    public ItemData vanilla(Container container) {
        var size = container.getContainerSize();
        ensureSpace(size);
        for (var i = 0; i < size; i++) items.add(container.getItem(i));
        return this;
    }

    /**
     * Adds items from a slot -> stack function.
     */
    public ItemData getter(IntFunction<ItemStack> getter, int size) {
        ensureSpace(size);
        for (var i = 0; i < size; i++) items.add(getter.apply(i));
        return this;
    }

    /**
     * Adds a single item stack.
     */
    public ItemData add(ItemStack stack) {
        items.add(stack);
        return this;
    }

    /**
     * Adds multiple item stacks.
     */
    public ItemData add(ItemStack... stacks) {
        ensureSpace(stacks.length);
        Collections.addAll(items, stacks);
        return this;
    }

    /**
     * Adds multiple item stacks.
     */
    public ItemData add(Collection<ItemStack> stacks) {
        items.addAll(stacks);
        return this;
    }

    /**
     * Ensure the internal list has empty space for the specified amount.
     */
    public ItemData ensureSpace(int length) {
        items.ensureCapacity(items.size() + length);
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    /** @hidden */
    protected final ArrayList<ItemStack> items = new ArrayList<>();

}
