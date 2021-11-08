package mcp.mobius.waila.api.event;

import java.awt.Rectangle;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Deprecated
@SuppressWarnings("DeprecatedIsStillUsed")
public class WailaRenderEvent extends Event {

    private final Rectangle position;

    public WailaRenderEvent(Rectangle position) {
        this.position = position;
    }

    public Rectangle getPosition() {
        return position;
    }

    /**
     * @deprecated use {@link IEventListener#onBeforeTooltipRender(PoseStack, Rectangle, ICommonAccessor, IPluginConfig, IEventListener.Canceller)}
     */
    @Cancelable
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

    /**
     * @deprecated use {@link IEventListener#onAfterTooltipRender(PoseStack, Rectangle, ICommonAccessor, IPluginConfig)}
     */
    @Deprecated
    public static class Post extends WailaRenderEvent {

        public Post(Rectangle position) {
            super(position);
        }

    }

}
