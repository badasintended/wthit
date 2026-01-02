package mcp.mobius.waila.api;

/**
 * The common side entrypoint for Waila plugins.
 * <p>
 * See {@link mcp.mobius.waila.api} documentation for more info.
 */
public interface IWailaCommonPlugin {

    void register(ICommonRegistrar registrar);

}
