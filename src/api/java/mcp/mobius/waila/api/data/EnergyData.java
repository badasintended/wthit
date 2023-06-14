package mcp.mobius.waila.api.data;

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
public final class EnergyData extends BuiltinData {

    public static final ResourceLocation ID = rl("energy");

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
     * Sets the default values that will be applied for objects from the specified namespace.
     */
    @BootstrapUnneeded
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

    @ApiStatus.Internal
    public EnergyData(FriendlyByteBuf buf) {
        this.stored = buf.readDouble();
        this.capacity = buf.readDouble();
    }

    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(stored);
        buf.writeDouble(capacity);
    }

    @ApiStatus.Internal
    public double stored() {
        return stored;
    }

    @ApiStatus.Internal
    public double capacity() {
        return capacity;
    }

}
