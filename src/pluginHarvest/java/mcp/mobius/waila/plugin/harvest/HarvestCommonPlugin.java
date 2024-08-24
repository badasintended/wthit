package mcp.mobius.waila.plugin.harvest;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.plugin.harvest.config.HarvestDisplayMode;
import mcp.mobius.waila.plugin.harvest.config.Options;

public class HarvestCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.featureConfig(Options.ENABLED, true);
        registrar.localConfig(Options.DISPLAY_MODE, HarvestDisplayMode.MODERN);
        registrar.localConfig(Options.CREATIVE, false);

        if (IApiService.INSTANCE.isDevEnv()) {
            registrar.localConfig(Options.DEV_DISABLE_CACHE, false);
        }
    }

}
