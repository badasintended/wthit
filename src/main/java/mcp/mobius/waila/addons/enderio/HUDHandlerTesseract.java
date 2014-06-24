package mcp.mobius.waila.addons.enderio;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerTesseract implements IWailaDataProvider {

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
		
		String channel = "%s : \u00a7f%s\u00a7r %s";
		String freq;
		String frequser;
		String owner;
		
		if (config.getConfig("enderio.channel")){
		
			if (accessor.getNBTData().hasKey("channelName")){
				freq = accessor.getNBTData().getString("channelName");
				
				if (accessor.getNBTData().hasKey("channelUser"))
					frequser = "[ \u00a7f" + accessor.getNBTData().getString("channelUser") + " \u00a7r]";
				else
					frequser = "[ \u00a7f" + LangUtil.translateG("hud.msg.public") + " \u00a7r]";
					
			}
			else{
				freq     = LangUtil.translateG("hud.msg.none");
				frequser = "";
			}
			
			currenttip.add(String.format(channel, LangUtil.translateG("hud.msg.frequency"), freq, frequser));
		}

		if (config.getConfig("enderio.owner")){
			if (accessor.getNBTData().hasKey("owner"))
				currenttip.add(String.format("%s : \u00a7f%s", LangUtil.translateG("hud.msg.owner"), accessor.getNBTData().getString("owner")));
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
