package mcp.mobius.Waila_BetterBarrels;

import java.util.List;

import codechicken.nei.api.HUDAugmenterRegistry.Layout;
import codechicken.nei.api.IHighlightHandler;
import mcp.mobius.betterbarrels.common.TileEntityBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BetterBarrelsHUDHandler implements IHighlightHandler {

	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {

		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (entity instanceof TileEntityBarrel)){
			ItemStack stack = ((TileEntityBarrel)entity).storage.getItem();
			
			if (stack != null){
				currenttip.add(stack.getDisplayName());
			} else {
				currenttip.add("<Empty>");
			}
		}	
		return currenttip;
	}

}
