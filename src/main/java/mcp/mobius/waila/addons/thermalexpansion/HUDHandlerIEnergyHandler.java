package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import mcp.mobius.waila.api.ITaggedList;

public class HUDHandlerIEnergyHandler implements IWailaDataProvider {

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
		
		if (!config.getConfig("thermalexpansion.energyhandler")) return currenttip;
		if (!accessor.getNBTData().hasKey("Energy")) return currenttip;
		
		int energy    = accessor.getNBTInteger(accessor.getNBTData(), "Energy");
		int maxEnergy = accessor.getNBTInteger(accessor.getNBTData(), "MaxStorage");
		try {
			if ((maxEnergy != 0) && (((ITaggedList)currenttip).getEntries("RFEnergyStorage").size() == 0)){
				((ITaggedList)currenttip).add(String.format("%d / %d RF", energy, maxEnergy), "RFEnergyStorage");
			}
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		try{
			Integer energy = -1;
			Integer maxsto = -1;
			if (ThermalExpansionModule.IEnergyInfo.isInstance(te)){
				energy = (Integer) ThermalExpansionModule.IEnergyInfo_getCurStorage.invoke(te);
				maxsto = (Integer) ThermalExpansionModule.IEnergyInfo_getMaxStorage.invoke(te);
			} else if (ThermalExpansionModule.IEnergyProvider.isInstance(te)){
				energy = (Integer) ThermalExpansionModule.IEnergyProvider_getCurStorage.invoke(te, EnumFacing.DOWN);
				maxsto = (Integer) ThermalExpansionModule.IEnergyProvider_getMaxStorage.invoke(te, EnumFacing.DOWN);
			} else if (ThermalExpansionModule.IEnergyReceiver.isInstance(te)){
				energy = (Integer) ThermalExpansionModule.IEnergyReceiver_getCurStorage.invoke(te, EnumFacing.DOWN);
				maxsto = (Integer) ThermalExpansionModule.IEnergyReceiver_getMaxStorage.invoke(te, EnumFacing.DOWN);
			}

			tag.setInteger("Energy",     energy);
			tag.setInteger("MaxStorage", maxsto);
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
		return tag;
	}	
	
}
