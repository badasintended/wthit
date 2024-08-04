package mcp.mobius.waila.api.data;

import java.util.Objects;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IExtraService;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Adds fluid information to an object.
 */
@ApiStatus.NonExtendable
public abstract class FluidData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("fluid");
    public static final Type<FluidData> TYPE = IData.createType(ID);

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
     *
     * @see mcp.mobius.waila.api.fabric.FabricFluidData#of()
     * @see mcp.mobius.waila.api.forge.ForgeFluidData#of()
     * @see mcp.mobius.waila.api.neo.NeoFluidData#of()
     */
    @SuppressWarnings("JavadocReference")
    public static FluidData of(Unit unit) {
        return IExtraService.INSTANCE.createFluidData(null, unit, -1);
    }

    /**
     * Creates a fluid data.
     *
     * @param unit          the fluid unit of measurement
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link #add} more than the specified count
     *
     * @see mcp.mobius.waila.api.fabric.FabricFluidData#of()
     * @see mcp.mobius.waila.api.forge.ForgeFluidData#of()
     * @see mcp.mobius.waila.api.neo.NeoFluidData#of()
     */
    @SuppressWarnings("JavadocReference")
    public static FluidData of(Unit unit, int slotCountHint) {
        return IExtraService.INSTANCE.createFluidData(null, unit, slotCountHint);
    }

    /**
     * Creates a fluid data with platform-specific translator.
     * <p>
     * Use the helper methods from the platform specific API package.
     *
     * @param translator the translator that gets the fluid, nbt, and (optionally) amount from a platform-specific object
     *
     * @see mcp.mobius.waila.api.fabric.FabricFluidData#of()
     * @see mcp.mobius.waila.api.forge.ForgeFluidData#of()
     * @see mcp.mobius.waila.api.neo.NeoFluidData#of()
     */
    @SuppressWarnings("JavadocReference")
    public static <S> PlatformDependant<S> of(PlatformTranslator<S> translator) {
        return IExtraService.INSTANCE.createFluidData(translator, translator.unit(), -1);
    }

    /**
     * Creates a fluid data with platform-specific translator.
     * <p>
     * Use the helper methods from the platform specific API package.
     *
     * @param translator    the translator that gets the fluid, nbt, and (optionally) amount from a platform-specific object
     * @param slotCountHint hint of how many the slots probably are to minimize growing the list more
     *                      than necessary, the user can call {@link #add} more than the specified count
     *
     * @see mcp.mobius.waila.api.fabric.FabricFluidData#of(int)
     * @see mcp.mobius.waila.api.forge.ForgeFluidData#of(int)
     * @see mcp.mobius.waila.api.neo.NeoFluidData#of(int)
     */
    @SuppressWarnings("JavadocReference")
    public static <S> PlatformDependant<S> of(PlatformTranslator<S> translator, int slotCountHint) {
        return IExtraService.INSTANCE.createFluidData(translator, translator.unit(), slotCountHint);
    }

    /**
     * Adds a fluid entry.
     *
     * @param fluid    the fluid instance, will be normalized as the source fluid if it is a {@link FlowingFluid}
     * @param data     the fluid's NBT data, will <b>NOT</b> be modified so it safe to not {@linkplain  CompoundTag#copy() copy} it
     * @param stored   the stored amount of the fluid, in the specified unit
     * @param capacity the maximum capacity of this slot, in the specified unit
     */
    public FluidData add(Fluid fluid, DataComponentPatch data, double stored, double capacity) {
        Objects.requireNonNull(fluid, "Fluid can't be null");
        Objects.requireNonNull(data, "Data can't be null, use EMPTY");

        capacity = Math.max(capacity, 0.0);
        stored = Mth.clamp(stored, 0.0, capacity);
        if (capacity == 0) capacity = Double.POSITIVE_INFINITY;

        var source = fluid instanceof FlowingFluid flowing ? flowing.getSource() : fluid;
        implAdd(source, data, stored, capacity);
        return this;
    }

    @ApiStatus.NonExtendable
    public static abstract class PlatformDependant<T> extends FluidData {

        /**
         * Adds a fluid entry.
         *
         * @param stack    the platform-specific object
         * @param capacity the maximum capacity of this slot, in the platform's unit
         */
        public PlatformDependant<T> add(T stack, double capacity) {
            var translator = translator();
            return add(translator.fluid(stack), translator.data(stack), translator.amount(stack), capacity);
        }

        /**
         * Adds a fluid entry.
         *
         * @param variant  the platform-specific object
         * @param stored   the stored amount of the fluid, in the platform's unit
         * @param capacity the maximum capacity of this slot, in the platform's unit
         */
        public PlatformDependant<T> add(T variant, double stored, double capacity) {
            var translator = translator();
            return add(translator.fluid(variant), translator.data(variant), stored, capacity);
        }

        @Override
        public PlatformDependant<T> add(Fluid fluid, DataComponentPatch data, double stored, double capacity) {
            super.add(fluid, data, stored, capacity);
            return this;
        }

    }

    public enum Unit {

        /**
         * 1 bucket = 1000 millibuckets. Used in Forge.
         */
        MILLIBUCKETS("mB"),

        /**
         * 1 bucket = 81000 droplets. Used in Fabric.
         */
        DROPLETS("dp");

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
        DataComponentPatch data();

    }

    @ApiStatus.OverrideOnly
    public interface CauldronDescriptor {

        @Nullable
        FluidData getCauldronFluidData(BlockState state);

    }

    @ApiStatus.Experimental
    @ApiStatus.OverrideOnly
    public interface PlatformTranslator<T> {

        /**
         * Returns the unit of this platform.
         */
        Unit unit();

        /**
         * Returns the fluid of the platform object.
         */
        Fluid fluid(T t);

        /**
         * Returns the nbt of the platform object.
         */
        DataComponentPatch data(T t);

        /**
         * Returns the amount of the platform object.
         *
         * @throws UnsupportedOperationException if the platform doesn't store amount information on this type,
         *                                       forcing user to use {@link PlatformDependant#add(Object, double, double)}
         */
        double amount(T t) throws UnsupportedOperationException;

    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    /** @hidden */
    protected abstract PlatformTranslator<Object> translator();

    /** @hidden */
    protected abstract void implAdd(Fluid fluid, DataComponentPatch data, double stored, double capacity);

}
