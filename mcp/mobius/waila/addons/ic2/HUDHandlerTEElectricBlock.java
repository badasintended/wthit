package mcp.mobius.waila.addons.ic2;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerTEElectricBlock implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		try{
			String storedStr  = LangUtil.translateG("hud.msg.stored");
			String outputStr  = LangUtil.translateG("hud.msg.output");				
			
			/* EU Storage */
			if (ConfigHandler.instance().getConfig("ic2.storage")){
				double stored  = IC2Module.getStoredEnergy(accessor); 
				int    storage = IC2Module.TEEB_MaxStorage.getInt(accessor.getTileEntity());
				
				if ( stored >= 0.0 )
					currenttip.add(String.format("%s : \u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU", storedStr, Math.round(Math.min(stored,storage)), storage));
			}
			
			if (ConfigHandler.instance().getConfig("ic2.outputeu")){
				int production = IC2Module.TEEB_Output.getInt(accessor.getTileEntity());
					if ( production > 0)
						currenttip.add(String.format("%s : \u00a7f%d\u00a7r EU/t", outputStr, production));
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
