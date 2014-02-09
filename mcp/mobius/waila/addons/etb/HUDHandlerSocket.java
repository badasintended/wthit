package mcp.mobius.waila.addons.etb;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerSocket implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
			if (!config.getConfig("etb.displaydata")) return currenttip;
		
			try {
				int[]      sides = (int[])ETBModule.Socket_sides.get(accessor.getTileEntity());
				Object[] configs = (Object[])ETBModule.Socket_configs.get(accessor.getTileEntity());
				Item     module  = (Item)ETBModule.module.get(null);
				
				
				for( int s = 0; s < 6; s++){
						if (sides[s] != 0){
							
							int tank            = (Integer)ETBModule.SC_tank.get(configs[s]);
							int inventory       = (Integer)ETBModule.SC_inventory.get(configs[s]);
							boolean[] rsControl = (boolean[])ETBModule.SC_rsControl.get(configs[s]);
							boolean[] rsLatch   = (boolean[])ETBModule.SC_rsLatch.get(configs[s]);
							
							
							ItemStack stack = new ItemStack(module, 1, sides[s]);
							String tipstr = String.format("%-5s : %s ", ForgeDirection.getOrientation(s), stack.getDisplayName());
							
							String configstr = "[ ";
							
							if (tank != -1)
								configstr += "\u00a79" + String.valueOf(tank) + " ";

							if (inventory != -1)
								configstr += "\u00a7a" + String.valueOf(inventory) + " ";
							
							if (rsControl[0] || rsControl[1] || rsControl[2]){
								configstr += "\u00a7c";
								configstr += rsControl[0] ? "1" : "0";
								configstr += rsControl[1] ? "1" : "0";
								configstr += rsControl[2] ? "1" : "0";
								configstr += " ";
							}

							if (rsLatch[0] || rsLatch[1] || rsLatch[2]){
								configstr += "\u00a75";
								configstr += rsLatch[0] ? "1" : "0";
								configstr += rsLatch[1] ? "1" : "0";
								configstr += rsLatch[2] ? "1" : "0";
								configstr += " ";
							}							
							
							configstr += "\u00a7r]";
							
							if (!configstr.equals("[ \u00a7r]"))
								tipstr += " " + configstr;
							
							/*
							if ((tank != -1) && (inventory != -1))
								tipstr += String.format("[ \u00a79%d \u00a7a%d \u00a7r]", tank, inventory);
							else if (tank != -1)
								tipstr += String.format("[ \u00a79%d \u00a7r]", tank);
							else if (inventory != -1)
								tipstr += String.format("[ \u00a7a%d \u00a7r]", inventory);
							*/
							
							currenttip.add(tipstr);
						}
				}
			
				
			
		} catch (Exception e) {
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);			
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
