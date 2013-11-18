package mcp.mobius.waila.addons.buildcraft;

import java.util.List;

import codechicken.lib.lang.LangUtil;
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
			
			String maxPowerStr = LangUtil.translateG("hud.msg.maxpower");
			String triggerStr = LangUtil.translateG("hud.msg.trigger");
			
			Object powerReceiver = BCPowerAPIModule.IPowerReceptor_getPowerReceiver.invoke(accessor.getTileEntity(), ForgeDirection.UNKNOWN);
			if (powerReceiver != null){
				
				Float minEnergyRecv = (Float)BCPowerAPIModule.PR_getMinEnergyReceived.invoke(powerReceiver);
				Float maxEnergyRecv = (Float)BCPowerAPIModule.PR_getMaxEnergyReceived.invoke(powerReceiver);
				Float activation    = (Float)BCPowerAPIModule.PR_getActivationEnergy.invoke(powerReceiver);				
	
				
				if(accessor.getNBTData().hasKey("powerProvider") && accessor.getNBTData().getCompoundTag("powerProvider").hasKey("storedEnergy") && config.getConfig("bcapi.storage")){
					Float energyStored  = (Float)accessor.getNBTData().getCompoundTag("powerProvider").getFloat("storedEnergy");
					Float maxEnergyStor = (Float)BCPowerAPIModule.PR_getMaxEnergyStored.invoke(powerReceiver);
					if (maxEnergyStor != 0.0f)
						currenttip.add(String.format("%.1f / %.1f MJ", energyStored, maxEnergyStor));
				}
	
				if(accessor.getNBTData().hasKey("storedEnergy") && config.getConfig("bcapi.storage")){
					Float energyStored  = (Float)accessor.getNBTData().getFloat("storedEnergy");
					Float maxEnergyStor = (Float)BCPowerAPIModule.PR_getMaxEnergyStored.invoke(powerReceiver);
					if (maxEnergyStor != 0.0f)
						currenttip.add(String.format("%.1f / %.1f MJ", energyStored, maxEnergyStor));
				}			
				
				if (maxEnergyRecv != 0.0f && config.getConfig("bcapi.consump"))
					//currenttip.add(String.format("Min/Max Input : %.1f / %.1f MJ/t", minEnergyRecv, maxEnergyRecv));
					currenttip.add(String.format("%s : %.1f MJ/t", maxPowerStr, maxEnergyRecv));
				
				if (activation != 0.0f && config.getConfig("bcapi.trigger"))
					currenttip.add(String.format("%s : %.1f MJ", triggerStr, activation));			
			
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
