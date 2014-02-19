package mcp.mobius.waila.addons.thaumcraft;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.NBTUtil;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerIAspectContainer implements IWailaDataProvider {
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
		ItemStack headSlot = accessor.getPlayer().inventory.armorInventory[3];
		if (headSlot == null) return currenttip;
		if (!ThaumcraftModule.ItemGoggles.isInstance(headSlot.getItem())) return currenttip;
		
		NBTTagCompound tag = accessor.getNBTData();
		
		if (tag.hasKey("Aspect") && tag.hasKey("Amount") && !tag.getString("Aspect").equals("")){
			String aspect = Character.toUpperCase(tag.getString("Aspect").charAt(0)) + tag.getString("Aspect").substring(1);
			currenttip.add(this.getAspectString(tag, "Aspect", "Amount"));
		}

		if (tag.hasKey("aspect") && tag.hasKey("amount") && !tag.getString("aspect").equals("")){
			String aspect = Character.toUpperCase(tag.getString("aspect").charAt(0)) + tag.getString("aspect").substring(1);
			currenttip.add(this.getAspectString(tag, "aspect", "amount"));
		}		
		
		if (tag.hasKey("Aspects")){
			NBTTagList taglist = tag.getTagList("Aspects");
			for (int i = 0; i < taglist.tagCount(); i++){
				NBTTagCompound subtag = (NBTTagCompound)taglist.tagAt(i);
				if (subtag.hasKey("amount") && subtag.hasKey("key") && !subtag.getString("key").equals("")){
					currenttip.add(this.getAspectString(subtag, "key", "amount"));
				}
			}
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	private String getAspectString(NBTTagCompound tag, String keyAspect, String keyAmount){
		String aspect = Character.toUpperCase(tag.getString(keyAspect).charAt(0)) + tag.getString(keyAspect).substring(1);
		String amount = String.valueOf(NBTUtil.getNBTInteger(tag, keyAmount));
		
		return String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s",  aspect, amount);
	}
	
}
