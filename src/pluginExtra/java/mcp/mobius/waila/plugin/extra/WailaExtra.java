package mcp.mobius.waila.plugin.extra;

import java.util.HashSet;
import java.util.Set;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.extra.provider.EnergyProvider;
import mcp.mobius.waila.plugin.extra.provider.ItemProvider;
import mcp.mobius.waila.plugin.extra.provider.ProgressProvider;

public class WailaExtra implements IWailaPlugin {

    public static final Set<Class<? extends IData>> BOOTSTRAPPED = new HashSet<>();

    @Override
    public void register(IRegistrar registrar) {
        EnergyProvider.INSTANCE.register(registrar, 900);
        ProgressProvider.INSTANCE.register(registrar, 1500);
        ItemProvider.INSTANCE.register(registrar, 1550);
    }

}
