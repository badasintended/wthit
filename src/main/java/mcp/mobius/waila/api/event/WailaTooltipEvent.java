package mcp.mobius.waila.api.event;

import java.util.List;

import mcp.mobius.waila.api.ICommonAccessor;
import net.minecraft.text.Text;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired just before the Waila tooltip sizes are calculated. This is the last chance to make edits to
 * the information being displayed.
 * <p>
 * This event is not cancelable.
 * <p>
 * {@link #currentTip} - The current tooltip to be drawn.
 */
public class WailaTooltipEvent extends Event {

    private final List<Text> currentTip;
    private final ICommonAccessor accessor;

    public WailaTooltipEvent(List<Text> currentTip, ICommonAccessor accessor) {
        this.currentTip = currentTip;
        this.accessor = accessor;
    }

    public List<Text> getCurrentTip() {
        return currentTip;
    }

    public ICommonAccessor getAccessor() {
        return accessor;
    }

}
