package mcp.mobius.waila.addons.enderstorage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerStorage implements IWailaDataProvider {
	
	private static String[] colors = {"White", "Orange", "Magenta", "LBlue", "Yellow", "Lime", "Pink", "Gray", "LGray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black"};
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(config.getConfig("enderstorage.colors")){
			try{
				int freq = EnderStorageModule.TileFrequencyOwner_Freq.getInt(accessor.getTileEntity());
				int freqLeft   = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 0); 
				int freqCenter = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 1);
				int freqRight  = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 2);
				
				currenttip.add(String.format("%s/%s/%s", colors[freqLeft], colors[freqCenter], colors[freqRight]));
			} catch (Exception e){
				currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
			}
		}

		
		return currenttip;
	}

}
