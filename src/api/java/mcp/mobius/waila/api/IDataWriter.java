package mcp.mobius.waila.api;

import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to sync data.
 */
@ApiStatus.NonExtendable
public interface IDataWriter {

    /**
     * Modify raw NBT data to be synced.
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
    <D extends IData> void add(IData.Type<D> type, Consumer<Result<D>> consumer);

    /**
     * Immediately adds a typed data.
     * <p>
     * Use this method only for internal data that only you use, otherwise use {@link #add(IData.Type, Consumer)} instead.
     *
     * @param data the data to add
     *
     * @throws IllegalStateException if the data is already added, whether with {@link #add(IData.Type, Consumer)} or this method
     */
    void addImmediate(IData data);

    /**
     * Blocks all lower priority provider unconditionally.
     *
     * @param type the type of the data that wanted to be blocked
     */
    default void blockAll(IData.Type<? extends IData> type) {
        add(type, Result::block);
    }

    @ApiStatus.NonExtendable
    interface Result<T extends IData> {

        /**
         * Send the data.
         * This also blocks addition from lower priority providers.
         *
         * @throws NullPointerException  if data is null
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
