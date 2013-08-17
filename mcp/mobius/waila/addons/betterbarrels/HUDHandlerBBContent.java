package mcp.mobius.waila.addons.betterbarrels;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
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

public class HUDHandlerBBContent implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity entity = accessor.getTileEntity();
		
		if (!config.getConfig("bb.content")) return currenttip;
		
		if ((entity != null) && (BetterBarrelsModule.TileEntityBarrel.isInstance(entity))){
			ItemStack stack = null;
			try {
			 stack = (ItemStack) BetterBarrelsModule.IBarrelStorage_getItem.invoke(BetterBarrelsModule.TileEntityBarrel_Storage.get(entity));
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
}
