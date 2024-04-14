package mcp.mobius.waila.api;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Used to get data synced.
 */
@ApiStatus.NonExtendable
public interface IDataReader {

    /**
     * Returns raw NBT data synced.
     */
    CompoundTag raw();

    /**
     * Returns typed data synced.
     *
     * @param type the type of the data.
     */
    @Nullable <D extends IData> D get(IData.Type<D> type);

    /**
     * Invalidate the current data instance of type {@code D},
     * making {@link #get(IData.Type)} returns {@code null} until a new instance is synced.
     *
     * @param type the type of the data
     */
    <D extends IData> void invalidate(IData.Type<D> type);

}
