package mcp.mobius.waila.addons.projectred;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerRsGateLogic implements IWailaDataProvider {

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
		if (!config.getConfig("pr.showdata")) return currenttip;
		
		int orient = 0;
		int subID  = 0;
		int shape  = 0;
		int pmax   = 0;
		int val    = 0;
		int max    = 0;
		int dec    = 0;
		int inc    = 0;
		
		boolean found = false;
		
		if (!accessor.getNBTData().hasKey("parts")) return currenttip;
		NBTTagList parts = accessor.getNBTData().getTagList("parts"); 
		for (int i = 0; i < parts.tagCount(); i++){
			NBTTagCompound subtag = (NBTTagCompound)parts.tagAt(i);
			String id = subtag.getString("id");
			
			if (id.equals("pr_sgate") || id.equals("pr_igate") || 
				id.equals("pr_tgate") || id.equals("pr_bgate") || 
				id.equals("pr_agate") || id.equals("pr_rgate")){
				orient = accessor.getNBTInteger(subtag, "orient");
				subID  = accessor.getNBTInteger(subtag, "subID");
				shape  = accessor.getNBTInteger(subtag, "shape");
				
				if (subID == 17)
					pmax =  accessor.getNBTInteger(subtag, "pmax");
				
				if (subID == 19){
					val = accessor.getNBTInteger(subtag, "val");
					max = accessor.getNBTInteger(subtag, "max");
					dec = accessor.getNBTInteger(subtag, "dec");
					inc = accessor.getNBTInteger(subtag, "inc");
				}
					
					
				found  = true;
			}
		}		
		
		if (!found) return currenttip;		
		
		switch(subID){
		case 10:		
			currenttip.add(String.format("Delay : \u00a7f%d ticks", (int)Math.pow(2, shape)));
			break;	
			
		case 15:
			if (shape == 0)
				currenttip.add("Shape : \u00a7fOpen");
			if (shape == 1)
				currenttip.add("Shape : \u00a7fHalf closed");			
			if (shape == 2)
				currenttip.add("Shape : \u00a7fclosed");			
			break;
			
		case 17:
			currenttip.add(String.format("Timer : \u00a7f%d \u00a7rms", (pmax + 2) * 50));
			break;
		
		case 19:
		
			
			currenttip.add(String.format("Value : \u00a7f%d \u00a7r/ \u00a7f%d", val, max));
			currenttip.add(String.format("Step  : \u00a7f-%d \u00a7r/ \u00a7f+%d", dec, inc));
			
		default:
			break;
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
