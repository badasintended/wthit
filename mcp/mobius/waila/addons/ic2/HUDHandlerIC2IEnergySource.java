package mcp.mobius.waila.addons.ic2;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.API;
import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo.Layout;

public class HUDHandlerIC2IEnergySource implements IWailaDataProvider {

	/* Our 3 interfaces */
	private static Class IEnergyStorage = null;	
	private static Class IEnergySource  = null;
	
	/* Our interesting methods */
	private static Method IEnergySource_GetOutput    = null;
	
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
		int output   = -1;
		
		try{
			if (IEnergySource.isInstance(accessor.getTileEntity()))
				output = (Integer)(IEnergySource_GetOutput.invoke(IEnergySource.cast(accessor.getTileEntity())));			
		} catch (Exception e){
			mod_Waila.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergySource for display !.\n" + String.valueOf(e));
			return currenttip;				
		}

		if (ConfigHandler.instance().getConfig("ic2.outputeu") && (output != -1))
			currenttip.add(String.format("OUT : %s EU/t", output));
		
		if (config.getConfig("ic2.storage"))
			if (accessor.getNBTData().hasKey("storage") && !IEnergyStorage.isInstance(accessor.getTileEntity()))
				currenttip.add(String.format("Storage : %s EU", accessor.getNBTData().getShort("storage")));
			
		return currenttip;
	}	
	
	public static void register(){
		try{
			IEnergyStorage = Class.forName("ic2.api.tile.IEnergyStorage");			
			IEnergySource  = Class.forName("ic2.api.energy.tile.IEnergySource");
			IEnergySource_GetOutput    = IEnergySource.getMethod("getMaxEnergyOutput");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] IEnergySource class not found.");
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] One method was not found.");
			return;			
		}

		ExternalModulesHandler.instance().addConfig("IndustrialCraft2", "ic2.outputeu", "Max EU output");			
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIC2IEnergySource(), IEnergySource);
		
		mod_Waila.log.log(Level.INFO, "Waila module IndustrialCraft|Source succefully hooked.");
	}

		
	
}
