package mcp.mobius.waila.api.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;

/**
 * Adds an energy information to an object.
 */
public final class EnergyData implements IData {

    public static final ResourceLocation ID = BuiltinDataUtil.rl("energy");

    /**
     * The default energy name translation key.
     */
    public static final Component DEFAULT_NAME = Component.translatable(Tl.Tooltip.Extra.ENERGY);

    /**
     * The default energy bar color.
     */
    public static final int DEFAULT_COLOR = 0x710C00;

    /**
     * The default unit of energy that will be shown, depending on the platform.
     * <p>
     * <b>E</b> will be used on Fabric/Quilt and <b>FE</b> on Forge.
     */
    public static final String DEFAULT_UNIT = IApiService.INSTANCE.getDefaultEnergyUnit();

    /**
     * An infinite energy data.
     *
     * @see #newInfiniteProvider()
     */
    public static final EnergyData INFINITE = EnergyData.of(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    /**
     * Creates a {@linkplain IDataProvider data provider} that always returns infinite energy data.
     * <p>
     * Should probably be used with a higher (lower number) priority than the {@linkplain IRegistrar#DEFAULT_PRIORITY default}.
     * <p>
     * Along with this method, Waila also provides {@code waila:extra/infinite_energy}
     * tag that can be used for marking blocks, block entity types, or entity types to contain infinite energy.
     *
     * @see #INFINITE
     */
    public static <T> IDataProvider<T> newInfiniteProvider() {
        return (data, accessor, config) -> data.add(EnergyData.class, res -> res.add(INFINITE));
    }

    /**
     * Sets the default values that will be applied for objects from the specified namespace.
     */
    @ApiSide.ClientOnly
    public static Description describe(String namespace) {
        return IExtraService.INSTANCE.setEnergyDescFor(namespace);
    }

    /**
     * Creates a energy data.
     *
     * @param stored   the stored energy, from {@code 0.0} to {@linkplain Double#POSITIVE_INFINITY infinity}
     * @param capacity the energy storage capacity, from {@code 1.0} to {@linkplain Double#POSITIVE_INFINITY infinity}
     */
    public static EnergyData of(double stored, double capacity) {
        capacity = Math.max(capacity, 0.0);
        stored = Mth.clamp(stored, 0.0, capacity);
        if (capacity == 0) capacity = Double.POSITIVE_INFINITY;

        return new EnergyData(stored, capacity);
    }

    @ApiStatus.NonExtendable
    public interface Description {

        /**
         * Sets the translation key to be used to name the energy name.
         *
         * @see EnergyData#DEFAULT_NAME
         */
        Description name(Component name);

        /**
         * Sets the color of the bar, in {@code 0xRRGGBB} format.
         *
         * @see EnergyData#DEFAULT_COLOR
         */
        Description color(int color);

        /**
         * Sets the unit of energy.
         *
         * @see EnergyData#DEFAULT_UNIT
         */
        Description unit(String unit);

    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    private final double stored;
    private final double capacity;

    @ApiStatus.Internal
    private EnergyData(double stored, double capacity) {
        this.stored = stored;
        this.capacity = capacity;
    }

    /** @hidden */
    @ApiStatus.Internal
    public EnergyData(FriendlyByteBuf buf) {
        this.stored = buf.readDouble();
        this.capacity = buf.readDouble();
    }

    /** @hidden */
    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(stored);
        buf.writeDouble(capacity);
    }

    /** @hidden */
    @ApiStatus.Internal
    public double stored() {
        return stored;
    }

    /** @hidden */
    @ApiStatus.Internal
    public double capacity() {
        return capacity;
    }

}
