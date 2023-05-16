package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiSide.ClientOnly
@ApiStatus.NonExtendable
public interface IDataReader {

    /**
     * Returns raw NBT data synced from the server.
     */
    CompoundTag raw();

    /**
     * Returns typed data synced from the server.
     *
     * @param type the type of the data.
     */
    @Nullable <T extends IData> T get(Class<T> type);

}
