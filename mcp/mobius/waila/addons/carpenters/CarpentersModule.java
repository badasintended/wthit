package mcp.mobius.waila.addons.carpenters;

import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class CarpentersModule {

	public static Class TEBase = null;
	public static Class BlockCarpentersDoor = null;
	public static Class BlockCarpentersHatch = null;
	
	public static void register(){
		try{
			Class CarpentersBlocks = Class.forName("carpentersblocks.CarpentersBlocks");
			Waila.log.log(Level.INFO, "CarpentersBlocks mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[CarpentersBlocks] CarpentersBlocks mod not found.");
			return;
		}			
		
		try{
			TEBase    = Class.forName("carpentersblocks.tileentity.TEBase");
			BlockCarpentersDoor  = Class.forName("carpentersblocks.block.BlockCarpentersDoor");
			BlockCarpentersHatch = Class.forName("carpentersblocks.block.BlockCarpentersHatch");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[CarpentersBlocks] Class not found. " + e);
			return;
		} catch (Exception e) {
			Waila.log.log(Level.WARNING, "[CarpentersBlocks] Error while loading module. " + e);
			return;
		}			

		ModuleRegistrar.instance().addConfig("Carpenter's Blocks", "carpenters.hide");
		ModuleRegistrar.instance().registerStackProvider(new HUDCarpentersBlocksTE(), BlockCarpentersDoor);
		ModuleRegistrar.instance().registerStackProvider(new HUDCarpentersBlocksTE(), BlockCarpentersHatch);
		ModuleRegistrar.instance().registerSyncedNBTKey("data",    TEBase);
		ModuleRegistrar.instance().registerSyncedNBTKey("cover_6", TEBase);
	}	
	
}
