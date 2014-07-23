package mcp.mobius.waila.handlers;

import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaSummaryProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class SummaryProvider {

	private static SummaryProvider instance = null;	

	private SummaryProvider() {
		instance = this;
	}

	public static SummaryProvider instance(){
		if (SummaryProvider.instance == null)
			SummaryProvider.instance = new SummaryProvider();
		return SummaryProvider.instance;
	}

	public LinkedHashMap<String, String> getSummary(ItemStack stack, IWailaConfigHandler config) {
		LinkedHashMap<String, String> currentSummary = new LinkedHashMap<String, String>();
		
		for (IWailaSummaryProvider provider : ModuleRegistrar.instance().getSummaryProvider(stack.getItem())){
			currentSummary = provider.getSummary(stack, currentSummary, config);
		}
		return currentSummary;
	}	
}
