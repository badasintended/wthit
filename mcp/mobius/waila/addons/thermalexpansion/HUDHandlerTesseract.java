package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerTesseract implements IWailaDataProvider {

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
		
		if (config.getConfig("thermalexpansion.tesssendrecv")){
			String send = "Send : ";
			String recv = "Recv : ";
			
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Item.Mode")){
			case 0:
				send += "\u00a7aItem ";
				break;
			case 1:
				recv += "\u00a7aItem ";
				break;
			case 2:
				send += "\u00a7aItem ";
				recv += "\u00a7aItem ";
				break;
			}
	
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Fluid.Mode")){
			case 0:
				send += "\u00a79Fluid ";
				break;
			case 1:
				recv += "\u00a79Fluid ";
				break;
			case 2:
				send += "\u00a79Fluid ";
				recv += "\u00a79Fluid ";
				break;
			}
			
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Energy.Mode")){
			case 0:
				send += "\u00a7cEnergy";
				break;
			case 1:
				recv += "\u00a7cEnergy";
				break;
			case 2:
				send += "\u00a7cEnergy";
				recv += "\u00a7cEnergy";
				break;
			}		
			
			if (!send.equals("Send : "))
				currenttip.add(send);
			
			if (!send.equals("Recv : "))		
				currenttip.add(recv);
		}
		
		if (config.getConfig("thermalexpansion.tessfreq"))
			currenttip.add(String.format("Frequency : %d",accessor.getNBTInteger(accessor.getNBTData(), "Frequency")));		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
