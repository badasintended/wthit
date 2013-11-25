package mcp.mobius.waila.addons.ic2;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerTEBaseGenerator implements IWailaDataProvider {

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
			/* EU Storage */
			if (ConfigHandler.instance().getConfig("ic2.storage")){
				double stored  = IC2Module.getStoredEnergy(accessor); 
				int    storage = IC2Module.TEBG_MaxStorage.getShort(accessor.getTileEntity());		
			
				if ( stored >= 0.0 )
					currenttip.add(String.format("Stored : %d / %d EU", Math.round(Math.min(stored,storage)), storage));
			}
			
			if (ConfigHandler.instance().getConfig("ic2.outputeu")){
				int production = IC2Module.TEBG_Production.getInt(accessor.getTileEntity());
					if ( production > 0)
						currenttip.add(String.format("Production : %d EU/t", production));
			}
			
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
