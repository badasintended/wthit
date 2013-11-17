package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;


public class HUDHandlerISecureTile implements IWailaDataProvider {

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
		
		if (config.getConfig("thermalexpansion.owner")){
		
			String owner  = accessor.getNBTData().getString("Owner");
			int    iaccess = accessor.getNBTInteger(accessor.getNBTData(), "Access");
			
			String access = "INVALID";
			switch(iaccess){
			case 0:
				access = "Public";
				break;
			case 1:
				access = "Restricted";
				break;
			case 2:
				access = "Private";			
				break;
			}
			
			currenttip.add(String.format("Owner : \u00a7f%s\u00a7r [ %s ]", owner, access));
		
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	

}
