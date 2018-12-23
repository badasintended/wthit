package mcp.mobius.waila.api.event;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import net.fabricmc.fabric.util.HandlerArray;
import net.fabricmc.fabric.util.HandlerRegistry;

import java.awt.Rectangle;

/**
 * The base event for rendering the Waila tooltip. This provides the opportunity to do last minute changes to the tooltip.
 * <p>
 * All sub-events are fired from {@link mcp.mobius.waila.overlay.OverlayRenderer#renderOverlay(mcp.mobius.waila.overlay.Tooltip)}.
 * All sub-events are fired every render tick.
 * <p>
 * {@link #position} The position and size of the tooltip being rendered
 */
public class WailaRenderEvent {

    public static final HandlerRegistry<PreRender> WAILA_RENDER_PRE = new HandlerArray<>(PreRender.class);
    public static final HandlerRegistry<PostRender> WAILA_RENDER_POST = new HandlerArray<>(PostRender.class);

    public interface PreRender {
        boolean onPreRender(Pre event);
    }

    public interface PostRender {
        void onPostRender(Post event);
    }

    private final Rectangle position;

    private WailaRenderEvent(Rectangle position) {
        this.position = position;
    }

    public Rectangle getPosition() {
        return position;
    }

    /**
     * This event is fired just before the Waila tooltip is rendered and right after setting up the GL state in
     * {@link mcp.mobius.waila.overlay.OverlayRenderer#renderOverlay(mcp.mobius.waila.overlay.Tooltip)}
     * <p>
     * This event is cancelable.
     * If this event is canceled, the tooltip will not render.
     */
    public static class Pre extends WailaRenderEvent {

        private final IWailaCommonAccessor accessor;

        public Pre(IWailaCommonAccessor accessor, Rectangle position) {
            super(position);

            this.accessor = accessor;
        }

        public IWailaCommonAccessor getAccessor() {
            return accessor;
        }
    }

    /**
     * This event is fired just after the tooltip is rendered and right before the GL state is reset in
     * {@link mcp.mobius.waila.overlay.OverlayRenderer#renderOverlay(mcp.mobius.waila.overlay.Tooltip)}
     * This event is only fired if {@link Pre} is not cancelled.
     * <p>
     * This event is not cancelable.
     */
    public static class Post extends WailaRenderEvent {

        public Post(Rectangle position) {
            super(position);
        }
    }
}
