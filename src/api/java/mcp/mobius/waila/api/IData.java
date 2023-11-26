package mcp.mobius.waila.api;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

/**
 * A typed data for syncing complex data.
 * <p>
 * Register data types with {@link IRegistrar#addDataType}
 * <p>
 * For simple data, consider using {@linkplain IDataWriter#raw() raw NBT data} instead,
 * as it is easier to do and enough for most purpose.
 * <p>
 * See {@link mcp.mobius.waila.api.data} for built-in implementations.
 */
public interface IData {

    /**
     * Write current data to the buffer.
     *
     * @param buf the buffer
     */
    @ApiStatus.OverrideOnly
    void write(FriendlyByteBuf buf);

    /**
     * A byte buffer to data serializer, should be implemented as a reference to data constructor.
     */
    @FunctionalInterface
    @ApiStatus.OverrideOnly
    interface Serializer<T extends IData> {

        /**
         * Read data from the buffer.
         *
         * @param buf the buffer
         */
        T read(FriendlyByteBuf buf);

    }

}
