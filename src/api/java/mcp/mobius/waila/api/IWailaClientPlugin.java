package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;

/**
 * The client side entrypoint for Waila plugins.
 * <p>
 * See {@linkplain  mcp.mobius.waila.api package documentation} for more info.
 */
@ApiSide.ClientOnly
public interface IWailaClientPlugin {

    void register(IClientRegistrar registrar);

}
