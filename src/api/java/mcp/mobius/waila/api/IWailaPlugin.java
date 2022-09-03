package mcp.mobius.waila.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Main interface used for Waila plugins. Provides a valid instance of {@link IRegistrar}.
 * <p>
 * To register the plugin instance, create a file called {@code waila_plugins.json} in the root of your mod. <pre><code>
 * {
 *   // the plugin identifier, [namespace:path]
 *   "yourmodid:plugin": {
 *      // the path to the implementation class
 *      "initializer": "package.YourWailaPlugin",
 *
 *      // optional, decide the environment the plugin will loaded, options:
 *      // client    load plugin only on client and integrated server
 *      // server    load plugin only on dedicated server
 *      // *         load plugin on both client and dedicated server
 *      "side": "*",
 *
 *      // optional, the required mods that this plugin needs
 *      "required": ["othermodid"]
 *   }
 *
 *   // register multiple plugins!
 *   "yourmodid:another": {...}
 * }
 * </code></pre>
 */
@ApiStatus.OverrideOnly
public interface IWailaPlugin {

    void register(IRegistrar registrar);

}
