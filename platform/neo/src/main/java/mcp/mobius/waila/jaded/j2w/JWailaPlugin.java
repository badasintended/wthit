package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;

public class JWailaPlugin implements IWailaPlugin {

    public final snownee.jade.api.IWailaPlugin jade;

    public JWailaPlugin(snownee.jade.api.IWailaPlugin jade) {
        this.jade = jade;
    }

    @Override
    public void register(IRegistrar registrar) {
        jade.register(WailaCommonRegistration.instance());

        if (Waila.CLIENT_SIDE) {
            jade.registerClient(WailaClientRegistration.instance());
        }
    }

}
