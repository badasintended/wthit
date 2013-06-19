package mcp.mobius.waila.addons.buildcraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerBCEngine implements IWailaDataProvider {

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
		if (tag.hasKey("energyF")){
			//float output = tag.getFloat("energyF")*tag.getFloat("progress");
			float output = tag.getFloat("energyF");
			if (output > 1.0F)
				currenttip.add(String.format("Output : %.1f MJ",output));
			else
				currenttip.add(String.format("Output : %.2f MJ",output));
		}
		return currenttip;
	}

}
