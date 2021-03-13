package mcp.mobius.waila.overlay.fabric;

import java.awt.Rectangle;

import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.overlay.Tooltip;

public class OverlayRendererImpl {

    public static Rectangle onPreRender(Tooltip tooltip) {
        WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
        return WailaRenderEvent.WAILA_RENDER_PRE.invoker().onPreRender(preEvent) ? null : preEvent.getPosition();
    }

    public static void onPostRender(Rectangle position) {
        WailaRenderEvent.Post postEvent = new WailaRenderEvent.Post(position);
        WailaRenderEvent.WAILA_RENDER_POST.invoker().onPostRender(postEvent);
    }

}
