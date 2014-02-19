package mcp.mobius.waila.addons.thaumcraft;

import java.lang.reflect.Field;
import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThaumcraftModule {

	public static Class IAspectContainer = null;
	public static Class TileAlchemyFurnace = null;
	public static Class ItemGoggles = null;
	
	public static void register(){

		try{
			Class ModThaumcraft = Class.forName("thaumcraft.common.Thaumcraft");
			Waila.log.log(Level.INFO, "Thaumcraft mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[Thaumcraft] Thaumcraft mod not found.");
			return;
		}
		
		try{
			IAspectContainer   = Class.forName("thaumcraft.api.aspects.IAspectContainer");
			TileAlchemyFurnace = Class.forName("thaumcraft.common.tiles.TileAlchemyFurnace");
			ItemGoggles        = Class.forName("thaumcraft.common.items.armor.ItemGoggles");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[Thaumcraft] Class not found. " + e);
			return;
		} catch (Exception e){
			Waila.log.log(Level.WARNING, "[Thaumcraft] Unhandled exception." + e);
			return;			
		}
		
		//ModuleRegistrar.instance().addConfig("Thaumcraft", "thaumcraft.storedvis");		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), IAspectContainer);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), TileAlchemyFurnace);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", IAspectContainer);

	}

}
