package mcp.mobius.waila.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IWailaCommonPlugin} or {@link IWailaClientPlugin} instead.
 */
@Deprecated
@ApiStatus.OverrideOnly
public interface IWailaPlugin {

    void register(IRegistrar registrar);

}
