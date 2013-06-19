package mcp.mobius.waila.addons.buildcraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerBCPipes implements IWailaDataProvider {
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
		NBTTagCompound tag = accessor.getNBTData();
		if (this.hasPartialKey(tag, "internalNextPower") && config.getConfig("bc.powerpipe")){
			double power = Math.max(0, this.getPartialKeySum(tag, "internalNextPower"));
			currenttip.add(String.format("Power : %.1f MJ", power));
		}
		return currenttip;
	}

	public boolean hasPartialKey(NBTTagCompound tag, String key){
		for (Object k : tag.tagMap.keySet())
			if (((String)k).contains(key))
				return true;
		return false;
	}
	
	public double getPartialKeySum(NBTTagCompound tag, String key){
		double retVal = 0;
		for (Object k : tag.tagMap.keySet())
			if (((String)k).contains(key))
				retVal += tag.getDouble((String)k);
		return retVal;
	}	
	
}
