package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

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

		int ntransformers = this.getTransformerUpgrades(accessor.getNBTData());
		if (ntransformers > 0){
			maxinput = maxinput * (int)Math.pow(4.0D, (double)Math.min(3, ntransformers));
		}
		
		
		if (ConfigHandler.instance().getConfig("ic2.inputeuother") && (maxinput != -1) && !this.canUpgrade(accessor.getNBTData()))
			currenttip.add(String.format("IN : %s EU/t", maxinput));

		if (ConfigHandler.instance().getConfig("ic2.inputeumach") && (maxinput != -1) && this.canUpgrade(accessor.getNBTData()))
			currenttip.add(String.format("IN : %s EU/t", maxinput));		
		
		if (config.getConfig("ic2.storage"))
			if (accessor.getNBTData().hasKey("energy") && !IC2Module.IEnergyStorage.isInstance(accessor.getTileEntity()))
				currenttip.add(String.format("Storage : %s EU", accessor.getNBTData().getInteger("energy")));		
		
		return currenttip;
	}
	
	public boolean canUpgrade(NBTTagCompound tag){
		if (tag.hasKey("InvSlots"))
			if (tag.getCompoundTag("InvSlots").hasKey("upgrade"))
				return true;
		return false;
	}
	
	public int getTransformerUpgrades(NBTTagCompound tag){
		if (!canUpgrade(tag)) return -1;
		
		int ntransformers = 0;
		NBTTagList contents = tag.getCompoundTag("InvSlots").getCompoundTag("upgrade").getTagList("Contents");
		for (NBTTagCompound subtag : (List<NBTTagCompound>)contents.tagList){
			ItemStack currentSlot = new ItemStack(subtag.getShort("id"), subtag.getByte("Count"), subtag.getShort("Damage"));
			if (IC2Module.TransformerUpgradeStack.isItemEqual(currentSlot)){
				ntransformers += currentSlot.stackSize;
			}
		}
		
		return ntransformers;
	}
}
