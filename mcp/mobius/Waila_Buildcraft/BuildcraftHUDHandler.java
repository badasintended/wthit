package mcp.mobius.Waila_Buildcraft;

import java.util.List;

import codechicken.nei.api.HUDAugmenterRegistry.Layout;
import codechicken.nei.api.HUDAugmenterRegistry;
import codechicken.nei.api.IHighlightHandler;
import buildcraft.factory.TileTank;
import mcp.mobius.betterbarrels.common.TileEntityBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

public class BuildcraftHUDHandler implements IHighlightHandler {

	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop,	List<String> currenttip, Layout layout) {

		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (entity instanceof TileTank)){
			
			ILiquidTank tank = ((TileTank)entity).getTanks(ForgeDirection.UNKNOWN)[0];
			LiquidStack liquidStack = tank.getLiquid();
			int liquidAmount = liquidStack != null ? liquidStack.amount:0;
			int capacity     = tank.getCapacity();
			
			if (liquidStack != null && layout == HUDAugmenterRegistry.Layout.HEADER){
				String name = currenttip.get(0);
				name = name + " (" + liquidStack.asItemStack().getDisplayName() + ")";
				currenttip.set(0, name);
			}
			
			if (liquidStack == null && layout == HUDAugmenterRegistry.Layout.HEADER){
				String name = currenttip.get(0);
				name = name + " <Empty>";
				currenttip.set(0, name);
			}
			
			if (layout == HUDAugmenterRegistry.Layout.BODY)
				currenttip.add(String.valueOf(liquidAmount) + "/" + String.valueOf(capacity)  + " mB");

			
			/*
			LiquidStack liquidStack  = null;
			int         liquidAmount = 0;
			int         nTanks       = 1;
			int         maxpressure  = 0;
			int         minpressure  = 0;
			
			// First, we look for the bottom tank
			while(true){
				if (world.getBlockTileEntity(entity.xCoord, entity.yCoord - 1, entity.zCoord) instanceof TileTank)
					entity = world.getBlockTileEntity(entity.xCoord, entity.yCoord - 1, entity.zCoord);
				else
					break;
			}			

			//Now we are at the bottom of the tank. We get the liquid stack
			liquidStack = ((TileTank)entity).tank.getLiquid();
			if (liquidStack != null)
				liquidAmount += liquidStack.amount;
			
			while(true){
				if (world.getBlockTileEntity(entity.xCoord, entity.yCoord + 1, entity.zCoord) instanceof TileTank){
					
					nTanks += 1;
					entity = world.getBlockTileEntity(entity.xCoord, entity.yCoord + 1, entity.zCoord);
					
					maxpressure = Math.max(maxpressure, ((TileTank)entity).tank.getTankPressure());
					minpressure = Math.min(minpressure, ((TileTank)entity).tank.getTankPressure());
					
					LiquidStack currStack = ((TileTank)entity).tank.getLiquid();
					if (currStack != null)
						liquidAmount += currStack.amount;
				}
				else
					break;
			}			
			
			if (liquidStack != null && layout == HUDAugmenterRegistry.Layout.HEADER){
				String name = currenttip.get(0);
				name = name + " (" + liquidStack.asItemStack().getDisplayName() + ")";
				currenttip.set(0, name);
			}
			
			if (liquidStack == null && layout == HUDAugmenterRegistry.Layout.HEADER){
				String name = currenttip.get(0);
				name = name + " <Empty>";
				currenttip.set(0, name);
			}
			
			if (layout == HUDAugmenterRegistry.Layout.BODY){
				currenttip.add(String.valueOf(liquidAmount) + "/" + String.valueOf(nTanks*16000)  + " mB");
				currenttip.add(String.valueOf(maxpressure) + "/" +String.valueOf(minpressure));
			}
			*/
		}		
		
		return currenttip;
	}

}
