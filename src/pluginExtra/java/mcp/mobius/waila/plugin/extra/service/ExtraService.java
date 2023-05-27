package mcp.mobius.waila.plugin.extra.service;

import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.plugin.extra.WailaExtra;

public class ExtraService implements IExtraService {

    @Override
    public <T extends BuiltinData> void bootstrapData(Class<T> type) {
        WailaExtra.BOOTSTRAPPED.add(type);
    }

}
