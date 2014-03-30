package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerConduitItem implements IWailaDataProvider {

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
		
		if (!config.getConfig("thermalexpansion.conditemmode")) return currenttip;
		
		boolean found = false;
		int      mode  = -1;
		
		if (!accessor.getNBTData().hasKey("parts")) return currenttip;
		NBTTagList parts = accessor.getNBTData().getTagList("parts"); 
		for (int i = 0; i < parts.tagCount(); i++){
			NBTTagCompound subtag = (NBTTagCompound)parts.tagAt(i);
			String id = subtag.getString("id");
			
			if (id.contains("ConduitItem")){
				mode   = accessor.getNBTInteger(subtag, "Mode");
				found  = true;
			}
		}	
		
		if (!found) return currenttip;		
		
		if (mode == 0){}
		else if (mode == 1)
			currenttip.add("Mode : \u00a7fDense");
		else if (mode == 2)
			currenttip.add("Mode : \u00a7fVacuum");
		else if (mode == 3)
			currenttip.add("Mode : \u00a7fRound-Robin");			
		else
			currenttip.add("Mode : <UNKNOWN>");

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
