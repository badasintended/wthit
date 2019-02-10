package mcp.mobius.waila.api.event;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.mobius.waila.api.ICommonAccessor;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.GuiLighting;

import java.awt.Rectangle;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * The base event for rendering the Waila tooltip. This provides the opportunity to do last minute changes to the tooltip.
 * <p>
 * All sub-events are fired from {@link mcp.mobius.waila.overlay.OverlayRenderer#renderOverlay(mcp.mobius.waila.overlay.Tooltip)}.
 * All sub-events are fired every render tick.
 * <p>
 * {@link #position} The position and size of the tooltip being rendered
 */
public class WailaRenderEvent {

    private static MethodHandle loadGlState_;
    public static final Event<PreRender> WAILA_RENDER_PRE = EventFactory.createArrayBacked(PreRender.class,
            listeners -> event -> {
                for (PreRender listener : listeners) {
                    if (listener.onPreRender(event)) {
                        GuiLighting.enableForItems();
                        GlStateManager.enableRescaleNormal();
                        try {
                            loadGlState_.invoke();
                        } catch (Throwable e) {
                            // No-op
                        }
                        GlStateManager.enableDepthTest();
                        GlStateManager.popMatrix();
                        return true;
                    }
                }

                return  false;
            }
    );
    public static final Event<PostRender> WAILA_RENDER_POST = EventFactory.createArrayBacked(PostRender.class,
            listeners -> event -> {
                for (PostRender listener : listeners)
                    listener.onPostRender(event);
            }
    );

    static {
        try {
            Class overlayRenderer = Class.forName("mcp.mobius.waila.overlay.OverlayRenderer");
            Method method = overlayRenderer.getMethod("loadGLState");
            loadGlState_ = MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
