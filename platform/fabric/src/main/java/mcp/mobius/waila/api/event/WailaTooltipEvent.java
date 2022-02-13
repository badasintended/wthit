package mcp.mobius.waila.api.event;

import java.util.List;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.20")
@SuppressWarnings({"ClassCanBeRecord", "DeprecatedIsStillUsed"})
public class WailaTooltipEvent {

    /**
     * @deprecated use {@link IEventListener#onHandleTooltip(ITooltip, ICommonAccessor, IPluginConfig)}
     */
    @Deprecated
    public static final Event<HandleTooltip> WAILA_HANDLE_TOOLTIP = EventFactory.createArrayBacked(HandleTooltip.class,
        listeners -> event -> {
            for (HandleTooltip listener : listeners)
                listener.onTooltip(event);
        }
    );

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
