package mcp.mobius.waila.addons.thaumcraft;

import java.util.List;

import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;

public class HUDHandlerCapacitor implements IWailaDataProvider {

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
		if (config.getConfig("thaumcraft.storedvis")){
			try{
				short storedVis = ThaumcraftModule.TileCrystalCapacitor_StoredVis.getShort(accessor.getTileEntity());
				short maxVis    = ThaumcraftModule.TileCrystalCapacitor_MaxVis.getShort(accessor.getTileEntity());
		
				currenttip.add(String.format("%s/%s vis", storedVis, maxVis));
			}catch (Exception e){
				currenttip = WailaExceptionHandler.handleErr(e, accessor.getClass().getName(), currenttip);
			}
		}
		return currenttip;
	}

}
