package mcp.mobius.waila.api;

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
     * For more complex data, use {@linkplain #add(IData) typed data} instead.
     */
    CompoundTag raw();

    /**
     * Adds a typed data.
     * <p>
     * For simple data, consider using {@linkplain #raw() raw NBT data} instead,
     * as it is easier to do and enough for most purpose.
     * <p>
     * There can only be single instance of data with the same type at a time.
     *
     * @see IData
     */
    <T extends IData> void add(T data);

}
