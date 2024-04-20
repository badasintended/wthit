package mcp.mobius.waila.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Main interface used for Waila plugins. Provides a valid instance of {@link IRegistrar}.
 * <p>
 * <h3>Plugin Definitions</h3>
 * <p>
 * To register the plugin instance, create a file called {@code waila_plugins.json} or {@code wthit_plugins.json} in the root of your mod. <pre><code>
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
 *      "required": {
 *          "othermodid": "*",    // match any version
 *          "another_one": ">1.2" // match versions newer than 1.2, see below for more details
 *      },
 *
 *      // optional, whether the plugin is enabled by default. defaults to true
 *      "defaultEnabled": true
 *   }
 *
 *   // register multiple plugins!
 *   "yourmodid:another": {...}
 * }
 * </code></pre>
 *
 * <p>
 * <h3>Version Ranges</h3>
 * <p>
 * Waila only implements primitive operator ({@code <, <=, >, >=, =}) alongside logical and ({@code &&}) and or ({@code ||})
 * for its version ranges.
 *
 * @see <a href="https://github.com/unascribed/FlexVer">FlexVer project</a>
 */
@ApiStatus.OverrideOnly
public interface IWailaPlugin {

    void register(IRegistrar registrar);

}
