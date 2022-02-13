package mcp.mobius.waila.api.event;

import java.util.List;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated use {@link IEventListener#onHandleTooltip(ITooltip, ICommonAccessor, IPluginConfig)}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.20")
@SuppressWarnings("DeprecatedIsStillUsed")
public class WailaTooltipEvent extends Event {

    private final List<Component> currentTip;
    private final ICommonAccessor accessor;

    public WailaTooltipEvent(List<Component> currentTip, ICommonAccessor accessor) {
        this.currentTip = currentTip;
        this.accessor = accessor;
    }

    public List<Component> getCurrentTip() {
        return currentTip;
    }

    public ICommonAccessor getAccessor() {
        return accessor;
    }

    public interface HandleTooltip {

        void onTooltip(WailaTooltipEvent event);

    }

}
