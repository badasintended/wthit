package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.ApiSide;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.OverrideOnly
public interface IObjectPicker {

    /**
     * Returns the object that Waila will show the tooltip to, typically by raycasting from the camera's eye.
     * <p>
     * <b>Note:</b> {@link IPickerResults#add return} all objects in a line from cast origin up until
     * {@link IPickerAccessor#getMaxDistance() max distance}, that way Waila can try to show tooltip
     * for further object if the tooltip for nearer object is disabled for some reason.
     * <p>
     * Check for config value to save some processing time.
     *
     * @see WailaConstants#CONFIG_SHOW_BLOCK
     * @see WailaConstants#CONFIG_SHOW_ENTITY
     * @see WailaConstants#CONFIG_SHOW_FLUID
     */
    void pick(IPickerAccessor accessor, IPickerResults results, IPluginConfig config);

}
