package mcp.mobius.waila.addons.secretrooms;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDTileEntityCamo implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
	
		if (config.getConfig("secretrooms.hide")){
			NBTTagCompound tag = accessor.getNBTData();
			
			if (tag.hasKey("copyID") && tag.hasKey("copyMeta")){
				int copyID   = accessor.getNBTInteger(tag, "copyID");
				int copyMeta = accessor.getNBTInteger(tag, "copyMeta");			
				return new ItemStack(copyID, 1, copyMeta);
			}
		}
		
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
