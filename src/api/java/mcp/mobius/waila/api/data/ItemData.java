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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public final class ItemData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("item");
    public static final ResourceLocation CONFIG_SYNC_NBT = BuiltinDataUtil.rl("item.nbt");

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
        int size = container.getContainerSize();
        ensureSpace(size);
        for (int i = 0; i < size; i++) items.add(container.getItem(i));
        return this;
    }

    /**
     * Adds items from a slot -> stack function.
     */
    public ItemData getter(IntFunction<ItemStack> getter, int size) {
        ensureSpace(size);
        for (int i = 0; i < size; i++) items.add(getter.apply(i));
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
    public ItemData ensureSpace(int lenght) {
        items.ensureCapacity(items.size() + lenght);
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

    @ApiStatus.Internal
    public ItemData(FriendlyByteBuf buf) {
        this.config = null;

        syncNbt = buf.readBoolean();
        int size = buf.readVarInt();
        ensureSpace(size);

        for (int i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            Item item = buf.readById(Registry.ITEM);
            int count = buf.readVarInt();
            ItemStack stack = new ItemStack(item, count);
            if (syncNbt) stack.setTag(buf.readNbt());
            add(stack);
        }
    }

    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        boolean syncNbt = config.getBoolean(CONFIG_SYNC_NBT);
        buf.writeBoolean(syncNbt);
        buf.writeVarInt(items.size());

        for (ItemStack stack : items) {
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

    @ApiStatus.Internal
    public ArrayList<ItemStack> items() {
        return items;
    }

    @ApiStatus.Internal
    public boolean syncNbt() {
        return syncNbt;
    }

}
