package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.EnergyData;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IExtraService {

    IExtraService INSTANCE = Internals.loadService(IExtraService.class);

    <T extends BuiltinData> void bootstrapData(Class<T> type);

    <T extends BuiltinData> void assertDataBootstrapped(Class<T> type);

    EnergyData.Defaults setEnergyDefaultsFor(String namespace);

}
