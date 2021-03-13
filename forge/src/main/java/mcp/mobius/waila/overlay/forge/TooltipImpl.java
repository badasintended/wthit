package mcp.mobius.waila.overlay.forge;

import java.util.List;

import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import net.minecraft.text.Text;
import net.minecraftforge.common.MinecraftForge;

public class TooltipImpl {

    public static void onTooltip(List<Text> texts) {
        WailaTooltipEvent event = new WailaTooltipEvent(texts, DataAccessor.INSTANCE);
        MinecraftForge.EVENT_BUS.post(event);
    }

}
