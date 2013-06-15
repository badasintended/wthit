package mcp.mobius.waila.handlers;

import java.util.List;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerExternal implements IHighlightHandler {
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		int blockID = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
		Block block = Block.blocksList[blockID];
		
		if (IWailaBlock.class.isInstance(block))
			return ((IWailaBlock)block).getWailaStack(world, player, mop, (IWailaConfigHandler)ConfigHandler.instance());

		if(ExternalModulesHandler.instance().hasStackProviders(blockID))
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getStackProviders(blockID)){
				ItemStack retval = dataProvider.getWailaStack(world, player, mop, (IWailaConfigHandler)ConfigHandler.instance());
				if (retval != null)
					return retval;
			}
		
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		int   blockID = world.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
		Block block = Block.blocksList[blockID];
		
		if (IWailaBlock.class.isInstance(block))
			if (layout == Layout.HEADER)
				return ((IWailaBlock)block).getWailaHead(itemStack, world, player, mop, currenttip, (IWailaConfigHandler)ConfigHandler.instance());
			else if (layout == Layout.BODY)
				return ((IWailaBlock)block).getWailaBody(itemStack, world, player, mop, currenttip, (IWailaConfigHandler)ConfigHandler.instance());
		
		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(blockID))
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getHeadProviders(blockID))
				currenttip = dataProvider.getWailaHead(itemStack, world, player, mop, currenttip, (IWailaConfigHandler)ConfigHandler.instance());

		if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(blockID))
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getBodyProviders(blockID))
				currenttip = dataProvider.getWailaBody(itemStack, world, player, mop, currenttip, (IWailaConfigHandler)ConfigHandler.instance());			
			
		return currenttip;
	}

}
