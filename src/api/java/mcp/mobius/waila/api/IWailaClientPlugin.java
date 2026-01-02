package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;

/**
 * The client side entrypoint for Waila plugins.
 * <p>
 * See {@link mcp.mobius.waila.api} documentation for more info.
 */
@ApiSide.ClientOnly
public interface IWailaClientPlugin {

    void register(IClientRegistrar registrar);

}
