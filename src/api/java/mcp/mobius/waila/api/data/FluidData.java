package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class FluidData extends BuiltinData {

    public static final ResourceLocation ID = rl("fluid");

    /**
     * Describes how the specific fluid will be shown in the client.
     *
     * @throws IllegalArgumentException if the fluid is a {@link FlowingFluid} and is not the source fluid instance
     */
    @BootstrapUnneeded
    @ApiSide.ClientOnly
    public static <T extends Fluid> void describe(T fluid, Descriptor<T> descriptor) {
        if (fluid instanceof FlowingFluid flowing) {
            Preconditions.checkArgument(flowing == flowing.getSource(), "Not a source fluid");
        }

        IExtraService.INSTANCE.setFluidDescFor(fluid, descriptor);
    }

    /**
     * Describes how the fluids of the specified type will be shown in the client.
     */
    @BootstrapUnneeded
    @ApiSide.ClientOnly
    public static <T extends Fluid> void describe(Class<T> clazz, Descriptor<T> descriptor) {
        IExtraService.INSTANCE.setFluidDescFor(clazz, descriptor);
    }

    /**
     * Creates a fluid data.
     */
    public static FluidData of() {
        return new FluidData(new ArrayList<>());
    }

    /**
     * Creates a fluid data.
     *
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link #add} more than the specified count
     */
    public static FluidData of(int slotCountHint) {
        return new FluidData(new ArrayList<>(slotCountHint));
    }

    /**
     * Adds a fluid entry.
     *
     * @param fluid      the fluid instance, will be normalized as the source fluid if it is a {@link FlowingFluid}
     * @param nbt        the fluid's NBT data, will <b>NOT</b> be modified so it safe to not {@linkplain  CompoundTag#copy() copy} it
     * @param storedMb   the stored amount of the fluid, in milibuckets
     * @param capacityMb the maximum capacity of this slot, in milibuckets
     */
    public FluidData add(Fluid fluid, @Nullable CompoundTag nbt, double storedMb, double capacityMb) {
        capacityMb = Math.max(capacityMb, 0.0);
        storedMb = Mth.clamp(storedMb, 0.0, capacityMb);
        if (capacityMb == 0) capacityMb = Double.POSITIVE_INFINITY;

        Fluid source = fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
        entries.add(new Entry<>(source, nbt, storedMb, capacityMb));
        return this;
    }

    @ApiSide.ClientOnly
    @ApiStatus.OverrideOnly
    public interface Descriptor<T extends Fluid> {

        void describe(DescriptionContext<T> ctx, Description desc);

    }

    @ApiSide.ClientOnly
    @ApiStatus.NonExtendable
    public interface Description {

        /**
         * The text that will be used to name the fluid.
         */
        Description name(Component name);

        /**
         * The sprite that will be used to render the fluid.
         */
        Description sprite(TextureAtlasSprite sprite);

        /**
         * The tint that will be applied to the sprite.
         *
         * @param argb the tint, in 0xAARRGGBB
         */
        Description tint(int argb);

    }

    @ApiSide.ClientOnly
    @ApiStatus.NonExtendable
    public interface DescriptionContext<T extends Fluid> {

        /**
         * Returns the fluid instance.
         */
        T fluid();

        /**
         * Returns the fluid's NBT data.
         */
        @Nullable
        CompoundTag nbt();

    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    private final List<Entry<?>> entries;

    @ApiStatus.Internal
    private FluidData(List<Entry<?>> entries) {
        this.entries = entries;
    }

    @ApiStatus.Internal
    public FluidData(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        entries = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            int id = buf.readVarInt();
            Fluid fluid = BuiltInRegistries.FLUID.byId(id);
            CompoundTag nbt = buf.readNbt();
            double stored = buf.readDouble();
            double capacity = buf.readDouble();
            add(fluid, nbt, stored, capacity);
        }
    }

    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entries.size());

        for (Entry<?> entry : entries) {
            if (entry.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeId(BuiltInRegistries.FLUID, entry.fluid);
                buf.writeNbt(entry.nbt);
                buf.writeDouble(entry.stored);
                buf.writeDouble(entry.capacity);
            }
        }
    }

    @ApiStatus.Internal
    public List<Entry<?>> entries() {
        return entries;
    }

    @ApiStatus.Internal
    public static class Entry<T extends Fluid> implements DescriptionContext<T> {

        private final T fluid;
        private final @Nullable CompoundTag nbt;
        private final double stored;
        private final double capacity;

        private Entry(T fluid, @Nullable CompoundTag nbt, double stored, double capacity) {
            this.fluid = fluid;
            this.nbt = nbt;
            this.stored = stored;
            this.capacity = capacity;
        }

        public boolean isEmpty() {
            return fluid == Fluids.EMPTY || stored <= 0;
        }

        @Override
        public T fluid() {
            return fluid;
        }

        @Override
        @Nullable
        public CompoundTag nbt() {
            return nbt;
        }

        public double stored() {
            return stored;
        }

        public double capacity() {
            return capacity;
        }

    }

}
