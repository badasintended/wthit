package mcp.mobius.waila.api;

import java.util.function.Consumer;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ServerOnly
@ApiStatus.NonExtendable
public interface IDataWriter {

    /**
     * Modify raw NBT data to be synced to client.
     * <p>
     * <b>Note:</b> use a unique property name, as it can conflict with data from other plugins.
     * <p>
     * For more complex data, use {@linkplain #add typed data} instead.
     */
    CompoundTag raw();

    /**
     * Adds a typed data.
     * <p>
     * For simple data, consider using {@linkplain #raw() raw NBT data} instead,
     * as it is easier to do and enough for most purpose.
     *
     * @param type     the type of the data
     * @param consumer the consumer that will be called if necessary based on the provider
     *                 priority. If earlier provider already added the data, or decided to block
     *                 other provider, this will not be called.
     *
     * @see IData
     */
    <T extends IData> void add(Class<T> type, Consumer<Result<T>> consumer);

    @ApiSide.ServerOnly
    @ApiStatus.NonExtendable
    interface Result<T extends IData> {

        /**
         * Send the data to client.
         * This also blocks addition from lower priority providers.
         *
         * @throws NullPointerException if data is null
         * @throws IllegalStateException if called multiple times in the same closure
         */
        Result<T> add(T data);

        /**
         * Block lower priority providers from adding data, regardless if current provider
         * added a data or not. Useful for disabling this data type for specific objects.
         */
        Result<T> block();

    }

}
