package mcp.mobius.waila.addons.enderio;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerCapacitor implements IWailaDataProvider{

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
		
		try{
			
			
			Integer maxEnergyStored = (Integer)EnderIOModule.TCB_getMaxEnergyStored.invoke(accessor.getTileEntity());
			Float   energyStored    = (Float)EnderIOModule.TCB_getEnergyStored.invoke(accessor.getTileEntity());
			Integer maxIO           = (Integer)EnderIOModule.TCB_getMaxIO.invoke(accessor.getTileEntity());
			Integer maxInput        = (Integer)EnderIOModule.TCB_getMaxInput.invoke(accessor.getTileEntity());
			Integer maxOutput       = (Integer)EnderIOModule.TCB_getMaxOutput.invoke(accessor.getTileEntity());
			
			if (config.getConfig("enderio.storage"))
				currenttip.add(String.format("%.1f / %d MJ", energyStored, maxEnergyStored));
			
			if (config.getConfig("enderio.inout")){
				currenttip.add(String.format("Max IO : %d MJ/t ", maxIO));
				currenttip.add(String.format("Input  : %d MJ/t ", maxInput));
				currenttip.add(String.format("Output : %d MJ/t ", maxOutput));
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
