package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IDataProvider} instead.
 */
// TODO: Remove
@Deprecated(forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
@ApiSide.ServerOnly
@ApiStatus.OverrideOnly
public interface IServerDataProvider<T> extends IDataProvider<T> {

    void appendServerData(CompoundTag data, IServerAccessor<T> accessor, IPluginConfig config);

    @Override
    default void appendData(IDataWriter data, IServerAccessor<T> accessor, IPluginConfig config) {
        appendServerData(data.raw(), accessor, config);
    }

}
