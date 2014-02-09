package mcp.mobius.waila.addons.ic2;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerTETradeOMat implements IWailaDataProvider {

	//TODO : Well, redo the damn class, that's it.
	
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
		/*
		if (!ConfigHandler.instance().getConfig("ic2.tradeomat")) return currenttip;
		
		NBTTagCompound slots = accessor.getNBTData().getCompoundTag("InvSlots");
		NBTTagList     offer = slots.getCompoundTag("offer").getTagList("Contents");
		NBTTagList    demand = slots.getCompoundTag("demand").getTagList("Contents");		
		String offersStr  = LangUtil.translateG("hud.msg.offers");
		String demandsStr = LangUtil.translateG("hud.msg.demands");
		
		
		if (offer.tagCount() == 1){
			NBTTagCompound subtag = (NBTTagCompound)offer.tagAt(0);
			ItemStack is = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			currenttip.add(String.format("%s : \u00a7f%d\u00a7r x \u00a7f%s\u00a7r", offersStr, is.stackSize, is.getDisplayName()));
		}

		if (demand.tagCount() == 1){
			NBTTagCompound subtag = (NBTTagCompound)demand.tagAt(0);
			ItemStack is = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			currenttip.add(String.format("%s : \u00a7f%d\u00a7r x \u00a7f%s\u00a7r", demandsStr, is.stackSize, is.getDisplayName()));
		}		
		*/
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
