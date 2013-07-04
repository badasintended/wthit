package mcp.mobius.waila.addons.thaumcraft;

import java.lang.reflect.Field;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class ThaumcraftModule {

	public static Class BlockMagicalLeaves = null;
	public static Class BlockCustomPlant = null;
	
	public static Class TileCrystalCapacitor = null;
	public static Field TileCrystalCapacitor_StoredVis = null;
	public static Field TileCrystalCapacitor_MaxVis = null;		
	
	public static void register(){

		try{
			Class ModThaumcraft = Class.forName("thaumcraft.common.Thaumcraft");
			mod_Waila.log.log(Level.INFO, "Thaumcraft mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Thaumcraft] Thaumcraft mod not found.");
			return;
		}
		
		try{
			BlockMagicalLeaves = Class.forName("thaumcraft.common.world.BlockMagicalLeaves");
			BlockCustomPlant = Class.forName("thaumcraft.common.world.BlockCustomPlant");	
			
			TileCrystalCapacitor = Class.forName("thaumcraft.common.tiles.TileCrystalCapacitor");
			TileCrystalCapacitor_StoredVis = TileCrystalCapacitor.getField("storedVis");
			TileCrystalCapacitor_MaxVis    = TileCrystalCapacitor.getField("maxVis");			
			
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Class not found. " + e);
			return;
//		} catch (NoSuchMethodException e){
//			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Method not found." + e);
//			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Field not found." + e);
			return;			
//		} catch (Exception e){
//			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Unhandled exception." + e);
//			return;			
		}		
		ExternalModulesHandler.instance().addConfig("Thaumcraft", "thaumcraft.storedvis", "Capacitor storage");		
		ExternalModulesHandler.instance().registerStackProvider(new HUDHandlerLeaves(), BlockMagicalLeaves);
		ExternalModulesHandler.instance().registerStackProvider(new HUDHandlerPlants(), BlockCustomPlant);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerCapacitor(), TileCrystalCapacitor);		
		
	}

}
