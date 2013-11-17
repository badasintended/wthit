package mcp.mobius.waila.addons.ic2;

import java.util.List;
import java.util.logging.Level;

import codechicken.lib.lang.LangUtil;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;

public class HUDHandlerIC2IEnergySource implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		//if (accessor.getTileEntity() != null && TEElectricBlock.isInstance(accessor.getTileEntity())){
		int output   = -1;
		
		try{
			if (IC2Module.IEnergySource.isInstance(accessor.getTileEntity()))
				output = (Integer)(IC2Module.IEnergySource_GetOutput.invoke(IC2Module.IEnergySource.cast(accessor.getTileEntity())));			
		} catch (Exception e){
			mod_Waila.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergySource for display !.\n" + String.valueOf(e));
			return currenttip;				
		}

		if (ConfigHandler.instance().getConfig("ic2.outputeu") && (output != -1))
			currenttip.add(String.format("%s : %s EU/t", LangUtil.translateG("hud.msg.output"), output));
		
		if (config.getConfig("ic2.storage"))
			if (accessor.getNBTData().hasKey("storage") && !IC2Module.IEnergyStorage.isInstance(accessor.getTileEntity()))
				currenttip.add(String.format("%s : %s EU", LangUtil.translateG("hud.msg.storage"), accessor.getNBTInteger(accessor.getNBTData(), "storage"))); 
			
		return currenttip;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
}
