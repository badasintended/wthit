package mcp.mobius.waila.addons.projectred;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.utils.NBTUtil;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDFMPGateLogic implements IWailaFMPProvider {

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,	IWailaConfigHandler config) {
		if (!config.getConfig("pr.showdata")) return currenttip;
		
		int orient = 0;
		int subID  = 0;
		int shape  = 0;
		int pmax   = 0;
		int val    = 0;
		int max    = 0;
		int dec    = 0;
		int inc    = 0;		
		
		orient = NBTUtil.getNBTInteger(accessor.getNBTData(), "orient");
		subID  = NBTUtil.getNBTInteger(accessor.getNBTData(), "subID");
		shape  = NBTUtil.getNBTInteger(accessor.getNBTData(), "shape");
		
		if (subID == 17)
			pmax =  NBTUtil.getNBTInteger(accessor.getNBTData(), "pmax");
		
		if (subID == 19){
			val = NBTUtil.getNBTInteger(accessor.getNBTData(), "val");
			max = NBTUtil.getNBTInteger(accessor.getNBTData(), "max");
			dec = NBTUtil.getNBTInteger(accessor.getNBTData(), "dec");
			inc = NBTUtil.getNBTInteger(accessor.getNBTData(), "inc");
		}
		
		switch(subID){
		case 10:		
			currenttip.add(String.format("[Repeater]" + TAB + ALIGNRIGHT + WHITE + "%d" + GRAY + " ticks", (int)Math.pow(2, shape)));
			break;	
			
		case 15:
			if (shape == 0)
				currenttip.add("[Sensor]" + TAB + ALIGNRIGHT + WHITE + "Open");
			if (shape == 1)
				currenttip.add("[Sensor]" + TAB + ALIGNRIGHT + WHITE + "Half closed");			
			if (shape == 2)
				currenttip.add("[Sensor]" + TAB + ALIGNRIGHT + WHITE + "Closed");			
			break;
			
		case 17:
			currenttip.add(String.format("[Timer delay]" + TAB + ALIGNRIGHT + WHITE + "%d " + GRAY + "ms", (pmax + 2) * 50));
			break;
		
		case 19:
		
			
			currenttip.add(String.format("[Counter value]"+ TAB + ALIGNRIGHT + WHITE + "%d "  + GRAY + "/ " + WHITE + "%d", val, max));
			currenttip.add(String.format("[Counter step]" + TAB + ALIGNRIGHT + WHITE + "-%d " + GRAY + "/ " + WHITE + "+%d", dec, inc));
			
		default:
			break;
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
