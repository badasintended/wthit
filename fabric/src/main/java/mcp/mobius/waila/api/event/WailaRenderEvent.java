package mcp.mobius.waila.api.event;

import java.awt.Rectangle;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.hud.HudRenderer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

/**
 * The base event for rendering the Waila tooltip. This provides the opportunity to do last minute changes to the tooltip.
 * <p>
 * All sub-events are fired from {@link HudRenderer#render(MatrixStack, float)}.
 * All sub-events are fired every render tick.
 * <p>
 * {@link #position} The position and size of the tooltip being rendered
 */
public class WailaRenderEvent {

    public static final Event<PreRender> WAILA_RENDER_PRE = EventFactory.createArrayBacked(PreRender.class,
        listeners -> event -> {
            for (PreRender listener : listeners) {
                if (listener.onPreRender(event)) {
                    return true;
                }
            }

            return false;
        }
    );
    public static final Event<PostRender> WAILA_RENDER_POST = EventFactory.createArrayBacked(PostRender.class,
        listeners -> event -> {
            for (PostRender listener : listeners)
                listener.onPostRender(event);
        }
    );

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
     * {@link HudRenderer#render(MatrixStack, float)}
     * <p>
     * This event is cancelable.
     * If this event is canceled, the tooltip will not render.
     */
    public static class Pre extends WailaRenderEvent {

        private final ICommonAccessor accessor;

        public Pre(ICommonAccessor accessor, Rectangle position) {
            super(position);

            this.accessor = accessor;
        }

        public ICommonAccessor getAccessor() {
            return accessor;
        }

    }

    /**
     * This event is fired just after the tooltip is rendered and right before the GL state is reset in
     * {@link HudRenderer#render(MatrixStack, float)}
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
