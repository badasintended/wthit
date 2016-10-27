package mcp.mobius.waila.handlers;

import com.google.common.base.Strings;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaTooltipHandler {

    public static String blockNameWrapper = "\u00a7r\u00a7f%s";
    public static String fluidNameWrapper = "\u00a7r\u00a7f%s";
    public static String entityNameWrapper = "\u00a7r\u00a7f%s";

    public static String metaDataThroughput = "%s:%d";
    public static String metaDataWrapper = String.format("\u00a77[%s]", metaDataThroughput);

    public static String modNameWrapper = "\u00A79\u00A7o%s";

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tooltipEvent(ItemTooltipEvent event) {String canonicalName = ModIdentification.nameFromStack(event.getItemStack());
        if (!Strings.isNullOrEmpty(modNameWrapper) && (!Strings.isNullOrEmpty(canonicalName)))
                event.getToolTip().add(String.format(modNameWrapper, canonicalName));
    }
}

