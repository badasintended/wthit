/**
 * The WTHIT API.
 *
 * <h3>Plugin Definitions</h3>
 * <p>
 * To register the plugin instance, create a file called {@code waila_plugins.json} or {@code wthit_plugins.json} in the root of your mod. <pre><code>
 * {
 *   // the plugin identifier, [namespace:path]
 *   "yourmodid:plugin": {
 *      "entrypoints" : {
 *        // common entrypoint, will always get called
 *        "common": "package.YourWailaCommonPlugin",
 *
 *        // client entrypoint, only called on client side
 *        "client": "package.YourWailaClientPlugin"
 *      },
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
 * @see mcp.mobius.waila.api.IWailaCommonPlugin
 * @see mcp.mobius.waila.api.IWailaClientPlugin
 */
package mcp.mobius.waila.api;
