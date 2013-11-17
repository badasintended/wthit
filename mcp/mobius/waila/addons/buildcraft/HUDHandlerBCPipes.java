package mcp.mobius.waila.addons.buildcraft;

import java.util.List;
import java.util.Map;

import codechicken.lib.lang.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.tools.AccessHelper;
import mcp.mobius.waila.tools.Reflect;

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
			currenttip.add(String.format("%s : %.1f MJ", LangUtil.translateG("hud.msg.power"), power));
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	public boolean hasPartialKey(NBTTagCompound tag, String key){

		Map tagMap;
		try{
			tagMap = (Map)AccessHelper.getFieldExcept("net.minecraft.nbt.NBTTagCompound", "field_74784_a", tag);
		} catch (Exception e){
			tagMap = (Map)AccessHelper.getField("net.minecraft.nbt.NBTTagCompound", "tagMap", tag);
		}		
		
		for (Object k : tagMap.keySet())
			if (((String)k).contains(key))
				return true;
		return false;
	}
	
	public double getPartialKeySum(NBTTagCompound tag, String key){
		double retVal = 0;

		Map tagMap;
		try{
			tagMap = (Map)AccessHelper.getFieldExcept("net.minecraft.nbt.NBTTagCompound", "field_74784_a", tag);
		} catch (Exception e){
			tagMap = (Map)AccessHelper.getField("net.minecraft.nbt.NBTTagCompound", "tagMap", tag);
		}
		
		for (Object k : tagMap.keySet())
			if (((String)k).contains(key))
				retVal += tag.getDouble((String)k);
		return retVal;
	}	
	
}
