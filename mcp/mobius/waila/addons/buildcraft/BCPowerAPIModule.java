package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class BCPowerAPIModule {

	public static Class  PowerHandler = null;
	public static Method PH_getMinEnergyReceived = null;
	public static Method PH_getMaxEnergyReceived = null;
	public static Method PH_getMaxEnergyStored = null;
	public static Method PH_getEnergyStored = null;
	public static Method PH_getActivationEnergy = null;	
	
	public static Class  IPowerReceptor = null;
	public static Method IPowerReceptor_getPowerReceiver = null;
	
	public static Class  PowerReceiver  = null;
	public static Method PR_getMinEnergyReceived = null;
	public static Method PR_getMaxEnergyReceived = null;
	public static Method PR_getMaxEnergyStored = null;
	public static Method PR_getEnergyStored = null;
	public static Method PR_getActivationEnergy = null;		
	
	public static void register(){	
		try{
			Class ModClass = Class.forName("buildcraft.api.power.IPowerReceptor");
			mod_Waila.log.log(Level.INFO, "Buildcraft Power API found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Buildcraft Power API] Buildcraft Power API mod not found.");
			return;
		}
		
		try{
			PowerHandler = Class.forName("buildcraft.api.power.PowerHandler");
			PH_getMinEnergyReceived = PowerHandler.getMethod("getMinEnergyReceived");
			PH_getMaxEnergyReceived = PowerHandler.getMethod("getMaxEnergyReceived");
			PH_getMaxEnergyStored   = PowerHandler.getMethod("getMaxEnergyStored");
			PH_getEnergyStored      = PowerHandler.getMethod("getEnergyStored");
			PH_getActivationEnergy  = PowerHandler.getMethod("getActivationEnergy");			
			
			IPowerReceptor = Class.forName("buildcraft.api.power.IPowerReceptor");
			IPowerReceptor_getPowerReceiver = IPowerReceptor.getMethod("getPowerReceiver", ForgeDirection.class);
			
			PowerReceiver = Class.forName("buildcraft.api.power.PowerHandler$PowerReceiver");
			PR_getMinEnergyReceived = PowerReceiver.getMethod("getMinEnergyReceived");
			PR_getMaxEnergyReceived = PowerReceiver.getMethod("getMaxEnergyReceived");
			PR_getMaxEnergyStored   = PowerReceiver.getMethod("getMaxEnergyStored");
			PR_getEnergyStored      = PowerReceiver.getMethod("getEnergyStored");
			PR_getActivationEnergy  = PowerReceiver.getMethod("getActivationEnergy");			
			
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
//			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ExternalModulesHandler.instance().addConfigRemote("BuildcraftAPI", "bcapi.storage");
		ExternalModulesHandler.instance().addConfig("BuildcraftAPI", "bcapi.consump");
		ExternalModulesHandler.instance().addConfig("BuildcraftAPI", "bcapi.trigger");
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIPowerReceptor(), IPowerReceptor);		

	}	
	
}
