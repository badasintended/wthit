package mcp.mobius.waila.addons.ic2;

import java.util.List;
import java.util.logging.Level;

import codechicken.lib.lang.LangUtil;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class HUDHandlerIC2IEnergySink implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		//if (accessor.getTileEntity() != null && TEElectricBlock.isInstance(accessor.getTileEntity())){
		int maxinput = -1;			
		try{
			if (IC2Module.IEnergySink.isInstance(accessor.getTileEntity()))
				maxinput = (Integer)(IC2Module.IEnergySink_GetInput.invoke(IC2Module.IEnergySink.cast(accessor.getTileEntity())));
		} catch (Exception e){
			mod_Waila.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergySink for display !.\n" + String.valueOf(e));
			return currenttip;				
		}

		int ntransformers = this.getTransformerUpgrades(accessor);
		if (ntransformers > 0){
			maxinput = maxinput * (int)Math.pow(4.0D, Math.min(3, ntransformers));
		}
		

		if (ConfigHandler.instance().getConfig("ic2.inputeuother") && (maxinput > 4096))
			currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.input"), LangUtil.translateG("hud.msg.any")));
			
		else if (ConfigHandler.instance().getConfig("ic2.inputeuother") && (maxinput != -1) && !this.canUpgrade(accessor.getNBTData()))
			currenttip.add(String.format("%s : %s EU/t", LangUtil.translateG("hud.msg.input"), maxinput));

		else if (ConfigHandler.instance().getConfig("ic2.inputeumach") && (maxinput != -1) && this.canUpgrade(accessor.getNBTData()))
			currenttip.add(String.format("%s : %s EU/t", LangUtil.translateG("hud.msg.input"), maxinput));		
		
		if (config.getConfig("ic2.storage"))
			if (accessor.getNBTData().hasKey("energy") && !IC2Module.IEnergyStorage.isInstance(accessor.getTileEntity()))
				currenttip.add(String.format("%s : %s EU", LangUtil.translateG("hud.msg.storage"), accessor.getNBTInteger(accessor.getNBTData(), "energy")));		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	public boolean canUpgrade(NBTTagCompound tag){
		if (tag.hasKey("InvSlots"))
			if (tag.getCompoundTag("InvSlots").hasKey("upgrade"))
				return true;
		return false;
	}
	
	public int getTransformerUpgrades(IWailaDataAccessor accessor){
		NBTTagCompound tag = accessor.getNBTData();
		if (!canUpgrade(tag)) return -1;
		
		int ntransformers = 0;
		NBTTagList contents = tag.getCompoundTag("InvSlots").getCompoundTag("upgrade").getTagList("Contents");
		for (NBTTagCompound subtag : (List<NBTTagCompound>)contents.tagList){
			ItemStack currentSlot = new ItemStack(accessor.getNBTInteger(subtag, "id"),accessor.getNBTInteger(subtag, "Count"),accessor.getNBTInteger(subtag, "Damage")); 
			if (IC2Module.TransformerUpgradeStack.isItemEqual(currentSlot)){
				ntransformers += currentSlot.stackSize;
			}
		}
		
		return ntransformers;
	}
}
