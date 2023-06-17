package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataWriter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Adds a crafting progress information to an object.
 */
public final class ProgressData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("progress");

    /**
     * Creates a progress data.
     * <p>
     * Do <b>NOT</b> {@linkplain IDataWriter.Result#add add} a data if the current progress is zero.
     *
     * @param ratio the ratio of the progress ranging from {@code 0.0f} to {@code 1.0f}
     */
    public static ProgressData ratio(float ratio) {
        return new ProgressData(Mth.clamp(ratio, 0f, 1f));
    }

    /**
     * Adds an item stack to the input list.
     */
    public ProgressData input(ItemStack stack) {
        input.add(stack);
        return this;
    }

    /**
     * Adds item stacks to the input list.
     */
    public ProgressData input(ItemStack... stacks) {
        ensureSpace(input, stacks.length);
        Collections.addAll(input, stacks);
        return this;
    }

    /**
     * Adds item stacks to the input list.
     */
    public ProgressData input(Collection<ItemStack> stacks) {
        input.addAll(stacks);
        return this;
    }

    /**
     * Adds an item stack from the specified slot to the input list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData input(int slot) {
        assertInventory();
        return input(inventory.apply(slot));
    }

    /**
     * Adds an item stack from the specified slots to the input list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData input(int... slots) {
        assertInventory();
        ensureSpace(input, slots.length);
        for (int slot : slots) input.add(inventory.apply(slot));
        return this;
    }

    /**
     * Adds an item stack to the output list.
     */
    public ProgressData output(ItemStack stack) {
        output.add(stack);
        return this;
    }

    /**
     * Adds item stacks to the output list.
     */
    public ProgressData output(ItemStack... stacks) {
        ensureSpace(output, stacks.length);
        Collections.addAll(output, stacks);
        return this;
    }

    /**
     * Adds item stacks to the output list.
     */
    public ProgressData output(Collection<ItemStack> stacks) {
        output.addAll(stacks);
        return this;
    }

    /**
     * Adds an item stack from the specified slot to the output list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData output(int slot) {
        assertInventory();
        return output(inventory.apply(slot));
    }

    /**
     * Adds an item stack from the specified slots to the output list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData output(int... slots) {
        assertInventory();
        ensureSpace(output, slots.length);
        for (int slot : slots) output.add(inventory.apply(slot));
        return this;
    }

    /**
     * Specify a slot to item stack getter to be used with {@link #input(int)} and {@link #output(int)}.
     */
    public ProgressData itemGetter(IntFunction<ItemStack> inventory) {
        this.inventory = inventory;
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    private final float ratio;
    private final ArrayList<ItemStack> input = new ArrayList<>();
    private final ArrayList<ItemStack> output = new ArrayList<>();
    private IntFunction<ItemStack> inventory;

    @ApiStatus.Internal
    private ProgressData(float ratio) {
        this.ratio = ratio;
    }

    @ApiStatus.Internal
    public ProgressData(FriendlyByteBuf buf) {
        this.ratio = buf.readFloat();

        int inputSize = buf.readVarInt();
        input.ensureCapacity(inputSize);
        for (int i = 0; i < inputSize; i++) {
            input.add(buf.readItem());
        }

        int outputSize = buf.readVarInt();
        output.ensureCapacity(outputSize);
        for (int i = 0; i < outputSize; i++) {
            output.add(buf.readItem());
        }
    }

    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(ratio);

        buf.writeVarInt(input.size());
        for (ItemStack stack : input) {
            buf.writeItem(stack);
        }

        buf.writeVarInt(output.size());
        for (ItemStack stack : output) {
            buf.writeItem(stack);
        }
    }

    @ApiStatus.Internal
    private void assertInventory() {
        Preconditions.checkState(inventory != null, "Call inventory() with stack getter first");
    }

    @ApiStatus.Internal
    private void ensureSpace(ArrayList<ItemStack> list, int toAdd) {
        list.ensureCapacity(list.size() + toAdd);
    }

    @ApiStatus.Internal
    public float ratio() {
        return ratio;
    }

    @ApiStatus.Internal
    public ArrayList<ItemStack> input() {
        return input;
    }

    @ApiStatus.Internal
    public ArrayList<ItemStack> output() {
        return output;
    }

}
