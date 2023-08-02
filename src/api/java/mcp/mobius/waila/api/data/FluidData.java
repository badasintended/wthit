package mcp.mobius.waila.api.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Adds fluid information to an object.
 */
public final class FluidData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("fluid");
    public static final ResourceLocation CONFIG_DISPLAY_UNIT = BuiltinDataUtil.rl("fluid.display_unit");

    /**
     * Describes how the specific fluid will be shown in the client.
     * <p>
     * Most platforms likely has API to do this already, so user might not need
     * to register their own implementation here.
     * <ul><li>
     * On Forge, {@code FluidType} will be used for fluid name and
     * {@code IClientFluidTypeExtensions} for fluid sprite.
     * </li><li>
     * On Fabric, {@code FluidVariantAttributeHandler} will be used for fluid name
     * and {@code FluidVariantRenderHandler} for fluid sprite.
     * </li></ul>
     *
     * @throws IllegalArgumentException if the fluid is a {@link FlowingFluid} and is not the source fluid instance
     */
    @ApiSide.ClientOnly
    public static <T extends Fluid> void describeFluid(T fluid, FluidDescriptor<T> descriptor) {
        if (fluid instanceof FlowingFluid flowing) {
            Preconditions.checkArgument(flowing == flowing.getSource(), "Not a source fluid");
        }

        IExtraService.INSTANCE.setFluidDescFor(fluid, descriptor);
    }

    /**
     * Describes how the fluids of the specified type will be shown in the client.
     * <p>
     * Most platforms likely has API to do this already, so user might not need
     * to register their own implementation here.
     * <ul><li>
     * On Forge, {@code FluidType} will be used for fluid name and
     * {@code IClientFluidTypeExtensions} for fluid sprite.
     * </li><li>
     * On Fabric, {@code FluidVariantAttributeHandler} will be used for fluid name
     * and {@code FluidVariantRenderHandler} for fluid sprite.
     * </li></ul>
     */
    @ApiSide.ClientOnly
    public static <T extends Fluid> void describeFluid(Class<T> clazz, FluidDescriptor<T> descriptor) {
        IExtraService.INSTANCE.setFluidDescFor(clazz, descriptor);
    }

    /**
     * Describes what fluids are contained in the specified cauldron-like block
     * that store fluid on its block state.
     * <p>
     * Some platforms have API to attach this information, so user might not need
     * to register their own implementation here.
     * <ul><li>
     * On Fabric, {@code CauldronFluidContent} is used to get the information.
     * </li></ul>
     */
    public static void describeCauldron(Block block, CauldronDescriptor descriptor) {
        IExtraService.INSTANCE.setCauldronDescFor(block, descriptor);
    }

    /**
     * Describes what fluids are contained in the specified cauldron-like block
     * type that store fluid on its block state.
     * <p>
     * Some platforms have API to attach this information, so user might not need
     * to register their own implementation here.
     * <ul><li>
     * On Fabric, {@code CauldronFluidContent} is used to get the information.
     * </li></ul>
     */
    public static void describeCauldron(Class<? extends Block> clazz, CauldronDescriptor descriptor) {
        IExtraService.INSTANCE.setCauldronDescFor(clazz, descriptor);
    }

    /**
     * Creates a fluid data.
     *
     * @param unit the fluid unit of measurement
     */
    public static FluidData of(Unit unit) {
        return new FluidData(new ArrayList<>(), unit);
    }

    /**
     * Creates a fluid data.
     *
     * @param unit          the fluid unit of measurement
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link #add} more than the specified count
     */
    public static FluidData of(Unit unit, int slotCountHint) {
        return new FluidData(new ArrayList<>(slotCountHint), unit);
    }

    /**
     * Adds a fluid entry.
     *
     * @param fluid    the fluid instance, will be normalized as the source fluid if it is a {@link FlowingFluid}
     * @param nbt      the fluid's NBT data, will <b>NOT</b> be modified so it safe to not {@linkplain  CompoundTag#copy() copy} it
     * @param stored   the stored amount of the fluid, in the specified unit
     * @param capacity the maximum capacity of this slot, in the specified unit
     */
    public FluidData add(Fluid fluid, @Nullable CompoundTag nbt, double stored, double capacity) {
        capacity = Math.max(capacity, 0.0);
        stored = Mth.clamp(stored, 0.0, capacity);
        if (capacity == 0) capacity = Double.POSITIVE_INFINITY;

        Fluid source = fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
        entries.add(new Entry<>(source, nbt, stored, capacity));
        return this;
    }

    public enum Unit {

        /**
         * 1 bucket = 1000 millibuckets. Used in Forge.
         */
        MILLIBUCKETS("mB"),

        /**
         * 1 bucket = 81000 droplets. Used in Fabric.
         */
        DROPLETS("d");

        public final String symbol;

        public static double convert(Unit from, Unit to, double amount) {
            if (from == to) return amount;

            return switch (to) {
                case MILLIBUCKETS -> amount / 81.0;
                case DROPLETS -> amount * 81.0;
            };
        }

        Unit(String symbol) {
            this.symbol = symbol;
        }

    }

    @ApiSide.ClientOnly
    @ApiStatus.OverrideOnly
    public interface FluidDescriptor<T extends Fluid> {

        void describeFluid(FluidDescriptionContext<T> ctx, FluidDescription desc);

    }

    @ApiSide.ClientOnly
    @ApiStatus.NonExtendable
    public interface FluidDescription {

        /**
         * The text that will be used to name the fluid.
         */
        FluidDescription name(Component name);

        /**
         * The sprite that will be used to render the fluid.
         */
        FluidDescription sprite(TextureAtlasSprite sprite);

        /**
         * The tint that will be applied to the sprite.
         *
         * @param argb the tint, in 0xAARRGGBB
         */
        FluidDescription tint(int argb);

    }

    @ApiSide.ClientOnly
    @ApiStatus.NonExtendable
    public interface FluidDescriptionContext<T extends Fluid> {

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

    @ApiStatus.OverrideOnly
    public interface CauldronDescriptor {

        @Nullable
        FluidData getCauldronFluidData(BlockState state);

    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    private final List<Entry<?>> entries;
    private final Unit unit;

    @ApiStatus.Internal
    private FluidData(List<Entry<?>> entries, Unit unit) {
        this.entries = entries;
        this.unit = unit;
    }

    /** @hidden */
    @ApiStatus.Internal
    public FluidData(FriendlyByteBuf buf) {
        unit = buf.readEnum(Unit.class);

        int size = buf.readVarInt();
        entries = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            int id = buf.readVarInt();
            Fluid fluid = Registry.FLUID.byId(id);
            CompoundTag nbt = buf.readNbt();
            double stored = buf.readDouble();
            double capacity = buf.readDouble();
            add(fluid, nbt, stored, capacity);
        }
    }

    /** @hidden */
    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(unit);
        buf.writeVarInt(entries.size());

        for (Entry<?> entry : entries) {
            if (entry.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeId(Registry.FLUID, entry.fluid);
                buf.writeNbt(entry.nbt);
                buf.writeDouble(entry.stored);
                buf.writeDouble(entry.capacity);
            }
        }
    }

    /** @hidden */
    public Unit unit() {
        return unit;
    }

    /** @hidden */
    @ApiStatus.Internal
    public List<Entry<?>> entries() {
        return entries;
    }

    /** @hidden */
    @ApiStatus.Internal
    public static class Entry<T extends Fluid> implements FluidDescriptionContext<T> {

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

    // -----------------------------------------------------------------------------------------------------------------------------------------------
    // TODO: Remove

    /**
     * @deprecated use {@link #of(Unit)}
     */
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.21")
    public static FluidData of() {
        return of(Unit.MILLIBUCKETS);
    }

    /**
     * @deprecated use {@link #of(Unit, int)}
     */
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval(inVersion = "1.21")
    public static FluidData of(int slotCountHint) {
        return of(Unit.MILLIBUCKETS, slotCountHint);
    }

}
