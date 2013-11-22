package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import net.minecraft.item.ItemStack;

public class IC2Module {

	/* Our interfaces */
	public static Class IEnergyStorage = null;		
	public static Class IEnergySink    = null;
	public static Class IEnergySource  = null;
	
	/* Methods interfaces */
	public static Method IEnergyStorage_GetCapacity = null;	
	public static Method IEnergySink_GetInput       = null;
	//public static Method IEnergySource_GetOutput    = null;
	
	/* Some required items */
	public static Class     IC2Items           = null;
	public static Field     TransformerUpgrade = null;
	public static ItemStack TransformerUpgradeStack = null;
	public static Field     EnergyStorageUpgrade = null;
	public static ItemStack EnergyStorageUpgradeStack = null;	

	/* Fix for doors */
	public static Class      BlockIC2Door = null;
	public static Field      ReinforcedDoor = null;	
	public static ItemStack  ReinforcedDoorStack = null;
	

	public static void register(){

		try{
			Class ModIndustrialCraft = Class.forName("ic2.core.IC2");
			mod_Waila.log.log(Level.INFO, "Industrialcraft2 mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[IC2] Industrialcraft2 mod not found.");
			return;
		}
		
		try{
			
			IEnergyStorage = Class.forName("ic2.api.tile.IEnergyStorage");				
			IEnergySink    = Class.forName("ic2.api.energy.tile.IEnergySink");
			IEnergySource  = Class.forName("ic2.api.energy.tile.IEnergySource");			
						
			IEnergySink_GetInput       = IEnergySink.getMethod("getMaxSafeInput");
			//IEnergySource_GetOutput    = IEnergySource.getMethod("getMaxEnergyOutput");
			IEnergyStorage_GetCapacity = IEnergyStorage.getMethod("getCapacity");
			
			IC2Items             = Class.forName("ic2.core.Ic2Items");
			TransformerUpgrade   = IC2Items.getDeclaredField("transformerUpgrade"); 
			EnergyStorageUpgrade = IC2Items.getDeclaredField("energyStorageUpgrade");
			
			TransformerUpgradeStack   = (ItemStack)TransformerUpgrade.get(null);
			EnergyStorageUpgradeStack = (ItemStack)EnergyStorageUpgrade.get(null);
			
			BlockIC2Door = Class.forName("ic2.core.block.BlockIC2Door");
			ReinforcedDoor = IC2Items.getDeclaredField("reinforcedDoor");
			ReinforcedDoorStack = (ItemStack)ReinforcedDoor.get(null);
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Method not found." + e);
			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Field not found." + e);
			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Unhandled exception." + e);
			return;			
		}		
		
		ExternalModulesHandler.instance().addConfigRemote("IndustrialCraft2", "ic2.inputeumach");
		ExternalModulesHandler.instance().addConfig("IndustrialCraft2", "ic2.inputeuother");
		ExternalModulesHandler.instance().addConfig("IndustrialCraft2", "ic2.outputeu");			
		ExternalModulesHandler.instance().addConfigRemote("IndustrialCraft2", "ic2.storage");	
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIC2IEnergySink(),    IEnergySink);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIC2IEnergySource(),  IEnergySource);		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIC2IEnergyStorage(), IEnergyStorage);
		ExternalModulesHandler.instance().registerStackProvider(new HUDHandlerDoor(), BlockIC2Door);
	}

}
