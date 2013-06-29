package mcp.mobius.waila.addons.thaumcraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.addons.ic2.HUDHandlerDoor;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2IEnergySink;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2IEnergySource;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2IEnergyStorage;
import net.minecraft.item.ItemStack;

public class ThaumcraftModule {

	public static Class BlockMagicalLeaves = null;
	public static Class BlockCustomPlant = null;
	
	
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
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Class not found. " + e);
			return;
//		} catch (NoSuchMethodException e){
//			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Method not found." + e);
//			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Field not found." + e);
//			return;			
//		} catch (Exception e){
//			mod_Waila.log.log(Level.WARNING, "[Thaumcraft] Unhandled exception." + e);
//			return;			
		}		
		ExternalModulesHandler.instance().registerStackProvider(new HUDHandlerLeaves(), BlockMagicalLeaves);
		ExternalModulesHandler.instance().registerStackProvider(new HUDHandlerPlants(), BlockCustomPlant);			
	}

}
