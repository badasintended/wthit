package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Adds item information to an object.
 */
public final class ItemData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("item");
    public static final ResourceLocation CONFIG_SYNC_NBT = BuiltinDataUtil.rl("item.nbt");
    public static final ResourceLocation CONFIG_MAX_HEIGHT = BuiltinDataUtil.rl("item.max_height");
    public static final ResourceLocation CONFIG_SORT_BY_COUNT = BuiltinDataUtil.rl("item.sort_by_count");

    /**
     * Creates an item data based from plugin config.
     */
    public static ItemData of(IPluginConfig config) {
        return new ItemData(config);
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

    private final IPluginConfig config;
    private final ArrayList<ItemStack> items = new ArrayList<>();
    private boolean syncNbt;

    @ApiStatus.Internal
    private ItemData(IPluginConfig config) {
        this.config = config;
    }

    /** @hidden */
    @ApiStatus.Internal
    public ItemData(FriendlyByteBuf buf) {
        this.config = null;

        syncNbt = buf.readBoolean();
        var size = buf.readVarInt();
        ensureSpace(size);

        for (var i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            var item = buf.readById(Registry.ITEM);
            var count = buf.readVarInt();
            var stack = new ItemStack(item, count);
            if (syncNbt) stack.setTag(buf.readNbt());
            add(stack);
        }
    }

    /** @hidden */
    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        var syncNbt = config.getBoolean(CONFIG_SYNC_NBT);
        buf.writeBoolean(syncNbt);
        buf.writeVarInt(items.size());

        for (var stack : items) {
            if (stack.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeId(Registry.ITEM, stack.getItem());
                buf.writeVarInt(stack.getCount());
                if (syncNbt) buf.writeNbt(stack.getTag());
            }
        }
    }

    /** @hidden */
    @ApiStatus.Internal
    public ArrayList<ItemStack> items() {
        return items;
    }

    /** @hidden */
    @ApiStatus.Internal
    public boolean syncNbt() {
        return syncNbt;
    }

}
