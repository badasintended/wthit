package mcp.mobius.waila.overlay.fabric;

import java.util.List;

import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import net.minecraft.text.Text;

public class TooltipImpl {

    public static void onTooltip(List<Text> texts) {
        WailaTooltipEvent event = new WailaTooltipEvent(texts, DataAccessor.INSTANCE);
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(event);
    }

}
