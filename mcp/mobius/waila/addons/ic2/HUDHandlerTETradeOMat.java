package mcp.mobius.waila.addons.ic2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerTETradeOMat implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		NBTTagCompound slots = accessor.getNBTData().getCompoundTag("InvSlots");
		NBTTagList     offer = slots.getCompoundTag("offer").getTagList("Contents");
		NBTTagList    demand = slots.getCompoundTag("demand").getTagList("Contents");		
		
		if (offer.tagCount() == 1){
			NBTTagCompound subtag = (NBTTagCompound)offer.tagAt(0);
			ItemStack is = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			currenttip.add(String.format("Offers : %d x %s", is.stackSize, is.getDisplayName()));
		}

		if (demand.tagCount() == 1){
			NBTTagCompound subtag = (NBTTagCompound)demand.tagAt(0);
			ItemStack is = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			currenttip.add(String.format("Demands : %d x %s", is.stackSize, is.getDisplayName()));
		}		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
