package mcp.mobius.waila.api.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerAccessor;

/**
 * Implementation of {@link IDataProvider} that blocks specified data types.
 * <p>
 * Usage of this class should probably be accompanied by a higher (lower number)
 * priority than the {@linkplain IRegistrar#DEFAULT_PRIORITY default}.
 */
public final class BlockingDataProvider<T> implements IDataProvider<T> {

    /**
     * @param types data types to block
     */
    @SafeVarargs
    public BlockingDataProvider(IData.Type<? extends IData>... types) {
        this.types = types;
    }

    private final IData.Type<? extends IData>[] types;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<T> accessor, IPluginConfig config) {
        for (var type : types) {
            data.blockAll(type);
        }
    }

}
