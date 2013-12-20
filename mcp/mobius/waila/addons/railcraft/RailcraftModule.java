package mcp.mobius.waila.addons.railcraft;

import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class RailcraftModule {
	public static Class  TileTankBase = null;	
	public static Class  ITankTile = null;
	public static Method ITankTile_getTank = null;
	//public static Class  StandardTank = null;	
	
	public static void register(){
		try{
			Class ModRailcraft = Class.forName("mods.railcraft.common.core.Railcraft");
			mod_Waila.log.log(Level.INFO, "Railcraft mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Railcraft] Railcraft mod not found.");
			return;
		}			
		
		try{
			TileTankBase = Class.forName("mods.railcraft.common.blocks.machine.beta.TileTankBase");
			ITankTile    = Class.forName("mods.railcraft.common.blocks.machine.ITankTile");
			ITankTile_getTank = ITankTile.getMethod("getTank");
			//StandardTank = Class.forName("mods.railcraft.common.fluids.tanks.StandardTank");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[Railcraft] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[Railcraft] Method not found." + e);
			return;			
		}
		
		ExternalModulesHandler.instance().addConfigRemote("Railcraft", "railcraft.fluidamount");		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTank(),  TileTankBase);
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerTank(),  TileTankBase);
	}
}
