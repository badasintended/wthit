package mcp.mobius.waila.plugin.extra;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.extra.provider.EnergyProvider;
import mcp.mobius.waila.plugin.extra.provider.FluidProvider;
import mcp.mobius.waila.plugin.extra.provider.ItemProvider;
import mcp.mobius.waila.plugin.extra.provider.ProgressProvider;

public class WailaPluginExtra implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        EnergyProvider.INSTANCE.register(registrar, 500);
        FluidProvider.INSTANCE.register(registrar, 550);
        ProgressProvider.INSTANCE.register(registrar, 1500);
        ItemProvider.INSTANCE.register(registrar, 1550);
    }

}
