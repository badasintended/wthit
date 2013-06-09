package mcp.mobius.waila.handlers;

import java.util.List;

import mcp.mobius.waila.mod_Waila;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.HUDAugmenterRegistry.Layout;
import codechicken.nei.api.IHighlightHandler;

public class NEIHUDHandler implements IHighlightHandler {

	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop,	List<String> currenttip, Layout layout) {
		String modName = mod_Waila.instance.getCanonicalName(itemStack);
		if (modName != null && !modName.equals(""))
			currenttip.add("\u00a79\u00a7o" + modName);
		return currenttip;
	};		
	
}
