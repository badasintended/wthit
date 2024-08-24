package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to send server-side data to the client.
 */
@ApiSide.ServerOnly
@ApiStatus.OverrideOnly
public interface IDataProvider<T> {

    /**
     * Callback used server side to return a custom synchronization data.
     *
     * @param data     current synchronization data
     * @param accessor contains the relevant context of the environment
     * @param config   current plugin configurations,
     *                 values <b>could be different</b> from the requesting client unless it was registered via
     *                 {@link ICommonRegistrar#syncedConfig}
     *
     * @see ICommonRegistrar#blockData(IDataProvider, Class, int)
     * @see ICommonRegistrar#entityData(IDataProvider, Class)
     * @see IBlockComponentProvider#appendDataContext(IDataWriter, IBlockAccessor, IPluginConfig)
     * @see IEntityComponentProvider#appendDataContext(IDataWriter, IEntityAccessor, IPluginConfig)
     */
    void appendData(IDataWriter data, IServerAccessor<T> accessor, IPluginConfig config);

}
