package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
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

public class HUDHandlerIC2IEnergyStorage implements IWailaDataProvider {

	/* Our 3 interfaces */
	private static Class IEnergyStorage = null;
	
	/* Our interesting methods */
	private static Method IEnergyStorage_GetCapacity = null;
	
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
		int stored   = -1;
		int capacity = -1;
		try{
			if (IEnergyStorage.isInstance(accessor.getTileEntity()))
				capacity = (Integer)(IEnergyStorage_GetCapacity.invoke(IEnergyStorage.cast(accessor.getTileEntity())));			
		} catch (Exception e){
			mod_Waila.log.log(Level.SEVERE, "[IC2] Unhandled exception trying to access an IEnergyStorage for display !.\n" + String.valueOf(e));
			return currenttip;				
		}

		if (accessor.getNBTData().hasKey("energy"))
			stored = accessor.getNBTData().getInteger("energy");

		if ((capacity != -1) && (stored != -1))
			currenttip.add(String.format("%s/%s EU", stored, capacity));

		if ((capacity == -1) && (stored != -1)){
			currenttip.add(String.format("Stored : %s EU", stored));
		}		
		
		return currenttip;
	}	
	
	public static void register(){
		try{
			IEnergyStorage = Class.forName("ic2.api.tile.IEnergyStorage");
			IEnergyStorage_GetCapacity = IEnergyStorage.getMethod("getCapacity");;
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] IEnergyStorage class not found.");
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] One method was not found.");
			return;			
		}
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIC2IEnergyStorage(), IEnergyStorage);
		
		mod_Waila.log.log(Level.INFO, "Waila module IndustrialCraft|Storage succefully hooked.");
	}

		
	
}
