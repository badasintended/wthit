package mcp.mobius.waila.api;

/**
 * The common side entrypoint for Waila plugins.
 * <p>
 * See {@linkplain  mcp.mobius.waila.api package documentation} for mor info.
 */
public interface IWailaCommonPlugin {

    void register(ICommonRegistrar registrar);

}
