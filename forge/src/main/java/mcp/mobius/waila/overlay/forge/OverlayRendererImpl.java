package mcp.mobius.waila.overlay.forge;

import java.awt.Rectangle;

import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.Tooltip;
import net.minecraftforge.common.MinecraftForge;

public class OverlayRendererImpl extends OverlayRenderer {

    public static Rectangle onPreRender(Tooltip tooltip) {
        WailaRenderEvent.Pre preEvent = new WailaRenderEvent.Pre(DataAccessor.INSTANCE, tooltip.getPosition());
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            return null;
        }
        return preEvent.getPosition();
    }

    public static void onPostRender(Rectangle position) {
        WailaRenderEvent.Post postEvent = new WailaRenderEvent.Post(position);
        MinecraftForge.EVENT_BUS.post(postEvent);
    }

}
