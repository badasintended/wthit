package mcp.mobius.waila.fabric;

import java.awt.Rectangle;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.event.WailaRenderEvent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import net.minecraft.network.chat.Component;

@SuppressWarnings("deprecation")
public enum FabricLegacyEventListener implements IEventListener {

    INSTANCE;

    @Override
    @SuppressWarnings("unchecked")
    public void onHandleTooltip(ITooltip tooltip, ICommonAccessor accessor, IPluginConfig config) {
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(new WailaTooltipEvent((List<Component>) tooltip, accessor));
    }

    @Override
    public void onBeforeTooltipRender(PoseStack matrices, Rectangle rect, ICommonAccessor accessor, IPluginConfig config, Canceller canceller) {
        if (WailaRenderEvent.WAILA_RENDER_PRE.invoker().onPreRender(new WailaRenderEvent.Pre(accessor, rect))) {
            canceller.cancel();
        }
    }

    @Override
    public void onAfterTooltipRender(PoseStack matrices, Rectangle rect, ICommonAccessor accessor, IPluginConfig config) {
        WailaRenderEvent.WAILA_RENDER_POST.invoker().onPostRender(new WailaRenderEvent.Post(rect));
    }

}
