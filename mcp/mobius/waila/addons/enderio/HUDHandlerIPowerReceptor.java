package mcp.mobius.waila.addons.enderio;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerIPowerReceptor implements IWailaDataProvider {

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
			
			Object powerReceiver = EnderIOModule.IPowerReceptor_getPowerReceiver.invoke(accessor.getTileEntity(), ForgeDirection.UNKNOWN);
			
			Float minEnergyRecv = (Float)EnderIOModule.PR_getMinEnergyReceived.invoke(powerReceiver);
			Float maxEnergyRecv = (Float)EnderIOModule.PR_getMaxEnergyReceived.invoke(powerReceiver);
			Float maxEnergyStor = (Float)EnderIOModule.PR_getMaxEnergyStored.invoke(powerReceiver);
			Float energyStored  = (Float)EnderIOModule.PR_getEnergyStored.invoke(powerReceiver); 
			Float activation    = (Float)EnderIOModule.PR_getActivationEnergy.invoke(powerReceiver);				
			
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
