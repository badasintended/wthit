/**
 * This package contains built-in data types that can be used to display components
 * in a standarized way.
 * <p>
 * First, inside {@link mcp.mobius.waila.api.IWailaPlugin#register IWailaPlugin.register},
 * call the {@link mcp.mobius.waila.api.data.BuiltinData#bootstrap BuiltinData.bootstrap}
 * with all builtin data types that are needed. This is so that the config screen will not
 * have options for unused data, as that would make it confusing to players. <pre><code>
 * public class MyWailaPlugin implements IWailaPlugin {
 *     &#064;Override
 *     public void register(IRegistrar registrar) {
 *         BuiltinData.bootstrap(EnergyData.class, ProgressData.class);
 *         registrar.addBlockData(dataProvider, MyBlockEntity.class);
 *     }
 * }
 * </code></pre>
 * <p>
 * Then implement {@link mcp.mobius.waila.api.IDataProvider IDataProvider},
 * and call {@link mcp.mobius.waila.api.IDataWriter#add IDataWriter.add}
 * with the data that wanted to be added. <pre><code>
 * public class MyDataProvider implements IDataProvider&lt;MyBlockEntity&gt; {
 *     &#064;Override
 *     public void appendData(IDataWriter data, ...) {
 *         data.add(EnergyData.class, () -> EnergyData.infinite());
 *     }
 * }
 * </code></pre>
 */
package mcp.mobius.waila.api.data;
