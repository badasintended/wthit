package mcp.mobius.waila.addons.enderio;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerInternalPowerReceptor implements IWailaDataProvider {

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
		
		try{
			Object powerHandler = EnderIOModule.IPR_getPowerHandler.invoke(accessor.getTileEntity());
			
			Float minEnergyRecv = (Float)EnderIOModule.PH_getMinEnergyReceived.invoke(powerHandler);
			Float maxEnergyRecv = (Float)EnderIOModule.PH_getMaxEnergyReceived.invoke(powerHandler);
			Float maxEnergyStor = (Float)EnderIOModule.PH_getMaxEnergyStored.invoke(powerHandler);
			Float energyStored  = (Float)EnderIOModule.PH_getEnergyStored.invoke(powerHandler); 
			Float activation    = (Float)EnderIOModule.PH_getActivationEnergy.invoke(powerHandler);			
			
			if (maxEnergyStor != 0.0f)
				currenttip.add(String.format("%.1f / %.1f MJ", energyStored, maxEnergyStor));
			
			if (maxEnergyRecv != 0.0f)
				//currenttip.add(String.format("Min/Max Input : %.1f / %.1f MJ/t", minEnergyRecv, maxEnergyRecv));
				currenttip.add(String.format("Comsumption : %.1f MJ/t", maxEnergyRecv));
			
			if (activation != 0.0f)
				currenttip.add(String.format("Activation    : %.1f MJ", activation));
			
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
