package mcp.mobius.waila.handlers;

import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaTooltipHandler {
	@SubscribeEvent
	@SideOnly(Side.CLIENT)	
	public void tooltipEvent(ItemTooltipEvent event) {
		String canonicalName = ModIdentification.nameFromStack(event.itemStack);
		if (canonicalName != null && !canonicalName.equals("") && !Loader.isModLoaded("JEI"))
			event.toolTip.add("\u00a79\u00a7o" +  canonicalName);
	}
}
