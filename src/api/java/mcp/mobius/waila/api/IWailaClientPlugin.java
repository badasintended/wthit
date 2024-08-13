package mcp.mobius.waila.api;

import lol.bai.badpackets.impl.marker.ApiSide;

/**
 * The client side entrypoint for Waila plugins.
 * <p>
 * See {@link mcp.mobius.waila.api} documentation for more info.
 */
@ApiSide.ClientOnly
public interface IWailaClientPlugin {

    void register(IClientRegistrar registrar);

}
