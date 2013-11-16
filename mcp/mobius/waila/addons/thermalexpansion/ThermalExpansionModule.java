package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class ThermalExpansionModule {

	public static Class  IEnergyHandler = null;
	public static Method IEnergyHandler_getMaxStorage = null;
	
	public static Class TileEnergyCell = null;
	//public static Field TileEnergyCell_energyReceive = null;
	//public static Field TileEnergyCell_energySend = null;	
	
	
	public static void register(){

		try{
			Class ModThermalExpansion = Class.forName("thermalexpansion.ThermalExpansion");
			mod_Waila.log.log(Level.INFO, "Thermal Expansion mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Thermal Expansion] ThermalExpansion mod not found.");
			return;
		}		
		
		try{
			IEnergyHandler = Class.forName("cofh.api.energy.IEnergyHandler");
			IEnergyHandler_getMaxStorage = IEnergyHandler.getMethod("getMaxEnergyStored", ForgeDirection.class);
			
			TileEnergyCell = Class.forName("thermalexpansion.block.energycell.TileEnergyCell");
			//TileEnergyCell_energyReceive = TileEnergyCell.getField("energyReceive");
			//TileEnergyCell_energySend = TileEnergyCell.getField("energySend");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Method not found." + e);
			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Field not found." + e);
//			return;			
//		} catch (Exception e){
//			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Unhandled exception." + e);
//			return;			
		}		
		
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler", "Show RF storage");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energycell", "Show ECell IN/OUT");
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyHandler);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerEnergyCell(), TileEnergyCell);					
		
	}
	
	
}
