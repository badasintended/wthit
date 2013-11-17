package mcp.mobius.waila.handlers.hud;

import java.util.List;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.tools.ModIdentification;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.ItemInfo.Layout;
import codechicken.nei.api.IHighlightHandler;

public class HUDHandlerWaila implements IHighlightHandler {

	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop,	List<String> currenttip, Layout layout) {
		
		if (layout == Layout.FOOTER){
			String modName = ModIdentification.nameFromStack(itemStack);
			if (modName != null && !modName.equals(""))
				currenttip.add("\u00A79\u00A7o" + modName);
		} else if (layout == Layout.HEADER && ConfigHandler.instance().getConfig(Constants.CFG_WAILA_METADATA, false)){
			if (currenttip.size() == 0)
				currenttip.add("< Unnamed >");
			else{
				String name = currenttip.get(0);
				currenttip.set(0, name + String.format(" %s:%s", world.getBlockId(mop.blockX, mop.blockY, mop.blockZ), world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)));
			}
		} 
		return currenttip;		
	}		
	
}
