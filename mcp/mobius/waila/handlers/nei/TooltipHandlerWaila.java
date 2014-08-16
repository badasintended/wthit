package mcp.mobius.waila.handlers.nei;

import java.util.List;

import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.forge.IContainerTooltipHandler;

public class TooltipHandlerWaila implements IContainerTooltipHandler {

	@Override
	public List<String> handleTooltipFirst(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		String canonicalName = ModIdentification.nameFromStack(itemstack);
		if (canonicalName != null && !canonicalName.equals(""))
			currenttip.add("\u00a79\u00a7o" + canonicalName);
		return currenttip;
	}

}
