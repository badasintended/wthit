package mcp.mobius.waila.addons.ic2;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

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
			currenttip.add(String.format("%s%s\u00a7f%d\u00a7r x \u00a7f%s\u00a7r", offersStr, TAB + ALIGNRIGHT, is.stackSize, is.getDisplayName()));
		}

		if (demand.tagCount() == 1){
			NBTTagCompound subtag = (NBTTagCompound)demand.tagAt(0);
			ItemStack is = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			currenttip.add(String.format("%s%s\u00a7f%d\u00a7r x \u00a7f%s\u00a7r", demandsStr, TAB + ALIGNRIGHT, is.stackSize, is.getDisplayName()));
		}		
		*/
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
