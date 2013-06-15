package mcp.mobius.waila.addons.betterbarrels;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
//import mcp.mobius.betterbarrels.common.TileEntityBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.api.ItemInfo.Layout;
import codechicken.nei.api.IHighlightHandler;

public class HUDHandlerBetterBarrels implements IHighlightHandler {

	static private Class  TileEntityBarrel         = null;
	static private Class  IBarrelStorage           = null;
	static private Field  TileEntityBarrel_Storage = null;
	static private Method IBarrelStorage_getItem   = null;
	
	@Override
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
		return null;
	}

	@Override
	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, List<String> currenttip, Layout layout) {

		TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		
		if ((entity != null) && (TileEntityBarrel.isInstance(entity))){
			ItemStack stack = null;
			try {
			 stack = (ItemStack) IBarrelStorage_getItem.invoke(TileEntityBarrel_Storage.get(entity));
			} catch (Exception e){
				mod_Waila.log.log(Level.SEVERE, "[BB] Unhandled exception trying to access a barrel storage for display !.\n" + String.valueOf(e));
				currenttip.add("<ERROR!>");
				return currenttip;
			}
			
			if (stack != null){
				currenttip.add(stack.getDisplayName());
			} else {
				currenttip.add("<Empty>");
			}
		}	
		return currenttip;
	}
	
	public static void register(){
		try{
			TileEntityBarrel = Class.forName("mcp.mobius.betterbarrels.common.TileEntityBarrel");
			IBarrelStorage   = Class.forName("mcp.mobius.betterbarrels.common.IBarrelStorage");
			TileEntityBarrel_Storage = TileEntityBarrel.getField("storage");
			IBarrelStorage_getItem   = IBarrelStorage.getMethod("getItem");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[BB] BetterBarrel classes not found.");
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[BB] Storage field not found.");			
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[BB] getItem() not found.");
		}
		
		if (IBarrelStorage_getItem != null){
			mod_Waila.log.log(Level.INFO, "Waila module BetterBarrel succefully hooked.");
			API.registerHighlightHandler(new HUDHandlerBetterBarrels(), ItemInfo.Layout.BODY);
		}
	}

}
