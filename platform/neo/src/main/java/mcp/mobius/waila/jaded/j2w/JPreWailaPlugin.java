package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.jaded.mixin.AccessPluginConfig;
import mcp.mobius.waila.jaded.mixin.AccessWailaClientRegistration;
import mcp.mobius.waila.jaded.mixin.AccessWailaCommonRegistration;

public class JPreWailaPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        AccessPluginConfig.wthit_setInstance(AccessPluginConfig.wthit_new());
        AccessWailaCommonRegistration.wthit_setInstance(AccessWailaCommonRegistration.wthit_new());
        AccessWailaClientRegistration.wthit_setInstance(AccessWailaClientRegistration.wthit_new());
    }

}
