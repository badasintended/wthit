package mcp.mobius.waila.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Main interface used for Waila plugins. Provides a valid instance of {@link IRegistrar}.
 * <p>
 * Include this class in the {@code initializer} field in your {@code fabric.mod.json} or {@code mods.toml} file.
 */
@ApiStatus.OverrideOnly
public interface IWailaPlugin {

    void register(IRegistrar registrar);

}
