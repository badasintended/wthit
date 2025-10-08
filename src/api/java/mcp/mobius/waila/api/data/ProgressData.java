package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Adds a crafting progress information to an object.
 */
@ApiStatus.NonExtendable
public abstract class ProgressData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("progress");
    public static final Type<ProgressData> TYPE = IData.createType(ID);

    public static final ResourceLocation CONFIG_TIME = BuiltinDataUtil.rl("progress.time");
    public static final ResourceLocation CONFIG_BAR_HEIGHT = BuiltinDataUtil.rl("progress.height");
    public static final ResourceLocation CONFIG_REPLACE_ITEMS = BuiltinDataUtil.rl("progress.replace_items");

    /**
     * Creates a progress data.
     * <p>
     * Prefer {@link #tick(int, int)} if the information present.
     * <p>
     * Do <b>NOT</b> {@linkplain IDataWriter.Result#add add} a data if the current progress is zero.
     *
     * @param ratio the ratio of the progress ranging from {@code 0.0f} to {@code 1.0f}
     */
    public static ProgressData ratio(float ratio) {
        return IExtraService.INSTANCE.createRatioProgressData(Mth.clamp(ratio, 0f, 1f));
    }

    /**
     * Creates a progress data.
     * <p>
     * Unlike {@link #ratio(float)}, this version also adds an estimated time to finish on the tooltip.
     * <p>
     * Do <b>NOT</b> {@linkplain IDataWriter.Result#add add} a data if the current progress is zero.
     *
     * @param current the current tick progress for the process
     * @param total   the estimated total tick for the process
     */
    public static ProgressData tick(int current, int total) {
        return IExtraService.INSTANCE.createTickProgressData(current, total);
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
        ensureInputSpace(stacks.length);
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
        return input(itemGetter.apply(slot));
    }

    /**
     * Adds an item stack from the specified slots to the input list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData input(int... slots) {
        assertInventory();
        ensureInputSpace(slots.length);
        for (var slot : slots) input.add(itemGetter.apply(slot));
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
        ensureOutputSpace(stacks.length);
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
        return output(itemGetter.apply(slot));
    }

    /**
     * Adds an item stack from the specified slots to the output list.
     * <p>
     * A stack getter needs to be specified with {@link #itemGetter(IntFunction)}.
     */
    public ProgressData output(int... slots) {
        assertInventory();
        ensureOutputSpace(slots.length);
        for (var slot : slots) output.add(itemGetter.apply(slot));
        return this;
    }

    /**
     * Specify a slot to item stack getter to be used with {@link #input(int)} and {@link #output(int)}.
     */
    public ProgressData itemGetter(IntFunction<ItemStack> getter) {
        this.itemGetter = getter;
        return this;
    }

    /**
     * Ensure the internal list has empty space for the specified amount.
     */
    public ProgressData ensureInputSpace(int length) {
        ensureSpace(input, length);
        return this;
    }

    /**
     * Ensure the internal list has empty space for the specified amount.
     */
    public ProgressData ensureOutputSpace(int length) {
        ensureSpace(output, length);
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    /** @hidden */
    protected final ArrayList<ItemStack> input = new ArrayList<>();

    /** @hidden */
    protected final ArrayList<ItemStack> output = new ArrayList<>();

    /** @hidden */
    protected IntFunction<ItemStack> itemGetter;

    @ApiStatus.Internal
    private void assertInventory() {
        Preconditions.checkState(itemGetter != null, "Call itemGetter() with stack getter first");
    }

    @ApiStatus.Internal
    private void ensureSpace(ArrayList<ItemStack> list, int toAdd) {
        list.ensureCapacity(list.size() + toAdd);
    }

}
