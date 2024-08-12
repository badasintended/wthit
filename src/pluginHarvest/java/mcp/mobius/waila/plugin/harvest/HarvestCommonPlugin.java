package mcp.mobius.waila.plugin.harvest;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.harvest.config.HarvestDisplayMode;
import mcp.mobius.waila.plugin.harvest.config.Options;

public class HarvestCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.addFeatureConfig(Options.ENABLED, true);
        registrar.addConfig(Options.DISPLAY_MODE, HarvestDisplayMode.MODERN);
        registrar.addConfig(Options.CREATIVE, false);
    }

}
