package mcp.mobius.waila.api.data;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.__internal__.IExtraService;

public abstract sealed class BuiltinData implements IData permits
    EnergyData,
    ItemData,
    ProgressData {

    /**
     * Bootstrap needed data types, making it available to use.
     */
    @SafeVarargs
    public static void bootstrap(Class<? extends BuiltinData>... types) {
        Preconditions.checkArgument(types.length > 0, "Need some types");

        for (Class<? extends BuiltinData> type : types) {
            IExtraService.INSTANCE.bootstrapData(type);
        }
    }

}
