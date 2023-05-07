package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ServerOnly
@ApiStatus.OverrideOnly
public interface IDataProvider<T> {

    /**
     * Callback used server side to return a custom synchronization {@link CompoundTag}.
     * <p>
     * Will only be called if the implementing class is registered via {@link IRegistrar#addBlockData} or {@link IRegistrar#addEntityData}.
     *
     * @param data     current synchronization data (might have been processed by other providers and might be processed by other providers)
     * @param accessor contains the relevant context of the environment
     * @param config   current plugin configurations,
     *                 values <b>could be different</b> from the requesting client unless it was registered via {@link IRegistrar#addSyncedConfig}
     */
    void appendData(IDataWriter data, IServerAccessor<T> accessor, IPluginConfig config);

}
