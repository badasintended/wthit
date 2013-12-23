package mcp.mobius.waila.handlers.hud;

import java.util.List;

import mcp.mobius.waila.overlay.WailaTickHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerNEI implements IHighlightHandler {

	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return WailaTickHandler.instance().identifiedHighlight;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		if (layout == Layout.HEADER){
			currenttip.addAll(WailaTickHandler.instance().currenttipHEAD);
		}
		if (layout == Layout.BODY){
			currenttip.addAll(WailaTickHandler.instance().currenttipBODY);
		}
		if (layout == Layout.FOOTER){
			currenttip.addAll(WailaTickHandler.instance().currenttipTAIL);
		}
		return currenttip;
	}

}
