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
    @Nullable <T extends IData> T get(Class<T> type);

    /**
     * Invalidate the current data instance of type {@code T},
     * making {@link #get(Class)} returns {@code null} until a new instance is synced.
     *
     * @param type the type of the data
     */
    <T extends IData> void invalidate(Class<T> type);

}
