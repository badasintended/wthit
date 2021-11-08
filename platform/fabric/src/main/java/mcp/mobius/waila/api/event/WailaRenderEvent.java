package mcp.mobius.waila.api.event;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Deprecated
@SuppressWarnings("DeprecatedIsStillUsed")
public class WailaRenderEvent {

    /**
     * @deprecated use {@link IEventListener#onBeforeTooltipRender(PoseStack, Rectangle, ICommonAccessor, IPluginConfig, IEventListener.Canceller)}
     */
    @Deprecated
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

    /**
     * @deprecated use {@link IEventListener#onAfterTooltipRender(PoseStack, Rectangle, ICommonAccessor, IPluginConfig)}
     */
    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static class Post extends WailaRenderEvent {

        public Post(Rectangle position) {
            super(position);
        }

    }

}
