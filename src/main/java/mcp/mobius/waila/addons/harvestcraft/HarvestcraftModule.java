package mcp.mobius.waila.addons.harvestcraft;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class HarvestcraftModule {
	public static Class TileEntityPamCrop  = null;

	
	public static void register(){
		try{
			Class PamHarvestCraft = Class.forName("assets.pamharvestcraft.PamHarvestCraft");
			Waila.log.log(Level.INFO, "PamHarvestCraft mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[PamHarvestCraft] PamHarvestCraft mod not found.");
			return;
		}			
		
		try{
			TileEntityPamCrop = Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARN, "[PamHarvestCraft] Class not found. " + e);
			return;
		}
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerPamCrop(),  TileEntityPamCrop);
	}
}
