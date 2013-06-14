package mcp.mobius.waila.handlers;

import java.util.List;

import mcp.mobius.waila.ConfigHandler;
import mcp.mobius.waila.api.IConfigHandler;
import mcp.mobius.waila.api.IWailaBlock;
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
		Block block = Block.blocksList[world.getBlockId(mop.blockX, mop.blockY, mop.blockZ)];
		if (IWailaBlock.class.isInstance(block))
			return ((IWailaBlock)block).getWailaStack(world, player, mop, (IConfigHandler)ConfigHandler.instance());
		else
			return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {
		Block block = Block.blocksList[world.getBlockId(mop.blockX, mop.blockY, mop.blockZ)];
		if (IWailaBlock.class.isInstance(block))
			if (layout == Layout.HEADER)
				return ((IWailaBlock)block).getWailaHead(itemStack, world, player, mop, currenttip, (IConfigHandler)ConfigHandler.instance());
			else if (layout == Layout.BODY)
				return ((IWailaBlock)block).getWailaBody(itemStack, world, player, mop, currenttip, (IConfigHandler)ConfigHandler.instance());
		
		return currenttip;
	}

}
