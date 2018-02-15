package mcp.mobius.waila.handlers;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Waila.MODID, value = Side.CLIENT)
public class VanillaTooltipHandler {

    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        String canonicalName = ModIdentification.nameFromStack(event.getItemStack());
        if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat) && !Strings.isNullOrEmpty(canonicalName))
            event.getToolTip().add(String.format(FormattingConfig.modNameFormat, canonicalName));
    }
}

