package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerConduitItem implements IWailaDataProvider {

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
		
		if (accessor.getNBTData().hasKey("Mode") && accessor.getNBTInteger(accessor.getNBTData(), "Mode") != 0){
			int mode = accessor.getNBTInteger(accessor.getNBTData(), "Mode");
			
			if (mode == 1)
				currenttip.add("Mode : \u00a7fDense");
			else if (mode == 2)
				currenttip.add("Mode : \u00a7fVacuum");
			else if (mode == 3)
				currenttip.add("Mode : \u00a7fRound-Robin");			
			else
				currenttip.add("Mode : <UNKNOWN>");
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
