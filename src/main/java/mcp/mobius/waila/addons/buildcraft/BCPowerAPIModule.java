package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

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
			Waila.log.log(Level.INFO, "Buildcraft Power API found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[Buildcraft Power API] Buildcraft Power API mod not found.");
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
			Waila.log.log(Level.WARN, "[EnderStorage] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			Waila.log.log(Level.WARN, "[EnderStorage] Method not found." + e);
			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
//			return;			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ModuleRegistrar.instance().addConfigRemote("BuildcraftAPI", "bcapi.storage");
		ModuleRegistrar.instance().addConfig("BuildcraftAPI", "bcapi.consump");
		ModuleRegistrar.instance().addConfig("BuildcraftAPI", "bcapi.trigger");
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIPowerReceptor(), IPowerReceptor);
		ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIPowerReceptor(), IPowerReceptor);

	}	
	
}
