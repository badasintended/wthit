package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IRayCastVectorProvider}
 */
// TODO: Remove
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.21")
@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IObjectPicker {

    void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config);

}
