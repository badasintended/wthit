package mcp.mobius.waila.handlers;

import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
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
			String modName = mod_Waila.instance.getModName(itemStack);
			if (modName != null && !modName.equals(""))
				currenttip.add("\u00a79\u00a7o" + modName);
		} else if (layout == Layout.HEADER && ConfigHandler.instance().getConfig("waila.showmetadata", false)){
			if (currenttip.size() == 0)
				currenttip.add("< Unnamed >");
			else{
				String name = currenttip.get(0);
				currenttip.set(0, name + String.format(" %s:%s", world.getBlockId(mop.blockX, mop.blockY, mop.blockZ), world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)));
			}
		} 
		return currenttip;		
	};		
	
}
