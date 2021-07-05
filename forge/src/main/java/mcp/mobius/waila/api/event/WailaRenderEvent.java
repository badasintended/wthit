package mcp.mobius.waila.api.event;

import java.awt.Rectangle;

import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.handler.Tooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * The base event for rendering the Waila tooltip. This provides the opportunity to do last minute changes to the tooltip.
 * <p>
 * All sub-events are fired from {@link Tooltip#render(MatrixStack, float)}.
 * All sub-events are fired every render tick.
 * <p>
 * {@link #position} The position and size of the tooltip being rendered
 */
public class WailaRenderEvent extends Event {

    private final Rectangle position;

    public WailaRenderEvent(Rectangle position) {
        this.position = position;
    }

    public Rectangle getPosition() {
        return position;
    }

    /**
     * This event is fired just before the Waila tooltip is rendered and right after setting up the GL state in
     * {@link Tooltip#render(MatrixStack, float)}
     * <p>
     * This event is cancelable.
     * If this event is canceled, the tooltip will not render.
     */
    @Cancelable
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
     * {@link Tooltip#render(MatrixStack, float)}
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