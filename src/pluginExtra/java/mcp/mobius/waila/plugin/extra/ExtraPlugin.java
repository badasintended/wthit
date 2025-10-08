package mcp.mobius.waila.plugin.extra;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.extra.provider.EnergyProvider;
import mcp.mobius.waila.plugin.extra.provider.FluidProvider;
import mcp.mobius.waila.plugin.extra.provider.ItemProvider;
import mcp.mobius.waila.plugin.extra.provider.ProgressProvider;

/**
 * waila:extra is a pretty special plugin.
 * <p>
 * Do NOT copy the code structure for your own plugins.
 * See the vanilla plugin for what a plugin should look like.
 */
public class ExtraPlugin implements IWailaCommonPlugin, IWailaClientPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        EnergyProvider.INSTANCE.register(registrar, 500);
        FluidProvider.INSTANCE.register(registrar, 550);
        ItemProvider.INSTANCE.register(registrar, 1550);
        ProgressProvider.INSTANCE.register(registrar, 1600);
    }

    @Override
    public void register(IClientRegistrar registrar) {
        EnergyProvider.INSTANCE.register(registrar, 500);
        FluidProvider.INSTANCE.register(registrar, 550);
        ItemProvider.INSTANCE.register(registrar, 1550);
        ProgressProvider.INSTANCE.register(registrar, 1600);
    }

}
