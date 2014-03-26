package mcp.mobius.waila.addons.ic2;

import java.util.List;

import codechicken.lib.lang.LangUtil;
import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerTEStandardMachine implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		try{
			
			IC2Upgrades upgrades = IC2Module.getUpgrades(accessor);			
			int    baseConsumption = IC2Module.TESM_DefaultEnergyConsume.getInt(accessor.getTileEntity());
			int    baseOpTime      = IC2Module.TESM_DefaultOperationLength.getInt(accessor.getTileEntity());
			int    baseStorage     = IC2Module.TESM_DefaultEnergyStorage.getInt(accessor.getTileEntity());
			
			long  consumption      = Math.round(Math.min(baseConsumption * Math.pow(1.6D, upgrades.overclocker), 2147483647));
			
		    double stackOpLen = baseOpTime * 64.0D * Math.pow(0.7D, upgrades.overclocker);
		    int     opPerTick = ((int)Math.min(Math.ceil(64.0D / stackOpLen), 2147483647.0D));
		    int        opTime = ((int)Math.round(stackOpLen * opPerTick / 64.0D));

		    int    storage    = Math.round(Math.min(baseStorage + 10000 * upgrades.storage + opTime * consumption, 2147483647));;
		    
			
			int       maxinput   = (Integer)IC2Module.IES_GetMaxSafeInput.invoke(accessor.getTileEntity()) * (int)Math.pow(4.0D, Math.min(4, upgrades.transform));
			double stored        = IC2Module.getStoredEnergy(accessor); 

			String storedStr   = LangUtil.translateG("hud.msg.stored");
			String powerStr    = LangUtil.translateG("hud.msg.power");			
			String maxPowerStr = LangUtil.translateG("hud.msg.maxpower");	
			
			/* EU Storage */
			if (ConfigHandler.instance().getConfig("ic2.storage")){
				if ( stored >= 0.0 )
					currenttip.add(String.format("%s%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU", storedStr, TAB + ALIGNRIGHT, Math.round(Math.min(stored,storage)), storage));
			}
		
			if (ConfigHandler.instance().getConfig("ic2.consump")){
				if ( consumption > 0)
					currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", powerStr, TAB + ALIGNRIGHT, consumption ));
			}
			
			if (ConfigHandler.instance().getConfig("ic2.inputeu")){
				if ( maxinput > 0)
					currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", maxPowerStr, TAB + ALIGNRIGHT, maxinput ));
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
