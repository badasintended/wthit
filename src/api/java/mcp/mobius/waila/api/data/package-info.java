/**
 * Contains built-in data types that can be used to display components
 * in a standarized way.
 * <p>
 * Implement {@link mcp.mobius.waila.api.IDataProvider IDataProvider},
 * and call {@link mcp.mobius.waila.api.IDataWriter#add IDataWriter.add}
 * with the data that wanted to be added. <pre>
 * public class MyDataProvider implements IDataProvider&lt;MyBlockEntity&gt; {
 *     &#064;Override
 *     public void appendData(IDataWriter data, ...) {
 *         data.add(EnergyData.class, res -> {
 *             res.add(EnergyData.of(...));
 *         });
 *     }
 * }
 * </pre>
 */
package mcp.mobius.waila.api.data;
