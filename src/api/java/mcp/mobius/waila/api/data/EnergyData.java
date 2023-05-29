package mcp.mobius.waila.api.data;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Adds an energy information to an object.
 */
public final class EnergyData extends BuiltinData {

    /**
     * The default energy name translation key.
     */
    public static final String DEFAULT_TRANSLATION_KEY = Tl.Tooltip.Extra.ENERGY;

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
     * <p>
     * Call this method inside {@link IWailaPlugin#register} after data
     * is {@linkplain BuiltinData#bootstrap bootstrapped}.
     */
    public static EnergyData.Defaults setDefaultsFor(String namespace) {
        IExtraService.INSTANCE.assertDataBootstrapped(EnergyData.class);
        return IExtraService.INSTANCE.setEnergyDefaultsFor(namespace);
    }

    /**
     * Creates a data with finite capacity.
     *
     * @param capacity the maximum capacity of energy storage, must be larger than 1
     */
    public static EnergyData capacity(long capacity) {
        Preconditions.checkArgument(capacity >= 1, "Energy capacity must be larger than 1");

        return new EnergyData(capacity);
    }

    /**
     * Creates a data that have an infinite capacity.
     */
    public static EnergyData endlessCapacity() {
        return new EnergyData(-1);
    }

    /**
     * Creates a data that have infinite capacity and stored energy.
     */
    public static EnergyData infinite() {
        EnergyData data = endlessCapacity();
        data.stored = -1;
        return data;
    }

    /**
     * Sets the stored energy in the object.
     *
     * @param stored must be larger than 0 and less or equal to the capacity.
     */
    public EnergyData stored(long stored) {
        Preconditions.checkState(this.stored != -1, "Stored energy can't be set on infinite data");
        Preconditions.checkArgument(stored >= 0, "Stored energy must be larger than 0");
        if (this.capacity != -1) Preconditions.checkArgument(stored <= this.capacity, "Stored energy can't be larger than capacity");

        this.stored = stored;
        return this;
    }

    /**
     * Sets the translation key to be used to name the energy name.
     *
     * @see #DEFAULT_TRANSLATION_KEY
     * @see #setDefaultsFor(String)
     * @see Defaults#nameTraslationKey(String)
     */
    public EnergyData nameTraslationKey(String nameTraslationKey) {
        this.nameTraslationKey = nameTraslationKey;
        return this;
    }

    /**
     * Sets the color of the bar, in {@code 0xRRGGBB} format.
     *
     * @see #DEFAULT_COLOR
     * @see #setDefaultsFor(String)
     * @see Defaults#color(int)
     */
    public EnergyData color(int rgb) {
        this.color = 0xFFFFFF & rgb;
        return this;
    }

    /**
     * Sets the unit of energy.
     *
     * @see #DEFAULT_UNIT
     * @see #setDefaultsFor(String)
     * @see Defaults#unit(String)
     */
    public EnergyData unit(String unit) {
        this.unit = unit;
        return this;
    }

    @ApiStatus.NonExtendable
    public interface Defaults {

        /**
         * Sets the translation key to be used to name the energy name.
         *
         * @see EnergyData#DEFAULT_TRANSLATION_KEY
         */
        Defaults nameTraslationKey(String nameTraslationKey);

        /**
         * Sets the color of the bar, in {@code 0xRRGGBB} format.
         *
         * @see EnergyData#DEFAULT_COLOR
         */
        Defaults color(int color);

        /**
         * Sets the unit of energy.
         *
         * @see EnergyData#DEFAULT_UNIT
         */
        Defaults unit(String unit);

    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------

    private final long capacity;
    private long stored;

    private String nameTraslationKey = null;
    private int color = -1;
    private String unit = null;

    @ApiStatus.Internal
    private EnergyData(long capacity) {
        this.capacity = capacity;
    }

    @ApiStatus.Internal
    public EnergyData(FriendlyByteBuf buf) {
        this.capacity = buf.readVarLong();
        this.stored = buf.readVarLong();
        this.nameTraslationKey = buf.readNullable(FriendlyByteBuf::readUtf);
        this.unit = buf.readNullable(FriendlyByteBuf::readUtf);

        if (buf.readBoolean()) {
            this.color = buf.readVarInt();
        }
    }

    @Override
    @ApiStatus.Internal
    public void write(FriendlyByteBuf buf) {
        buf.writeVarLong(capacity);
        buf.writeVarLong(stored);
        buf.writeNullable(nameTraslationKey, FriendlyByteBuf::writeUtf);
        buf.writeNullable(unit, FriendlyByteBuf::writeUtf);

        if (color != -1) {
            buf.writeBoolean(true);
            buf.writeVarInt(color);
        } else {
            buf.writeBoolean(false);
        }
    }

    @ApiStatus.Internal
    public long stored() {
        return stored;
    }

    @ApiStatus.Internal
    public long capacity() {
        return capacity;
    }

    @Nullable
    @ApiStatus.Internal
    public String nameTraslationKey() {
        return nameTraslationKey;
    }

    @ApiStatus.Internal
    public int color() {
        return color;
    }

    @Nullable
    @ApiStatus.Internal
    public String unit() {
        return unit;
    }

}
