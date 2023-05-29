package mcp.mobius.waila.plugin.extra.service;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.plugin.extra.WailaExtra;
import mcp.mobius.waila.plugin.extra.data.EnergyDefaults;

public class ExtraService implements IExtraService {

    @Override
    public <T extends BuiltinData> void bootstrapData(Class<T> type) {
        WailaExtra.BOOTSTRAPPED.add(type);
    }

    @Override
    public <T extends BuiltinData> void assertDataBootstrapped(Class<T> type) {
        Preconditions.checkState(WailaExtra.BOOTSTRAPPED.contains(type));
    }

    @Override
    public EnergyData.Defaults setEnergyDefaultsFor(String namespace) {
        EnergyDefaults defaults = new EnergyDefaults();
        EnergyDefaults.MAP.put(namespace, defaults);
        return defaults;
    }

}
