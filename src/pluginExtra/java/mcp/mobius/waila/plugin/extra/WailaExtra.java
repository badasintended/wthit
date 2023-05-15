package mcp.mobius.waila.plugin.extra;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.extra.provider.EnergyProvider;

public class WailaExtra implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        EnergyProvider.INSTANCE.register(registrar, 900);
    }

}
