package mcp.mobius.waila.api;

/**
 * Main interface used for Waila plugins. Provides a valid instance of {@link IRegistrar}.
 * <p>
 * Include this class in the {@code initializer} field in your {@code fabric.mod.json} or {@code mods.toml} file.
 */
public interface IWailaPlugin {

    void register(IRegistrar registrar);

}
