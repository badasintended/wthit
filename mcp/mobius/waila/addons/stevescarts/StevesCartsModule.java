package mcp.mobius.waila.addons.stevescarts;

import java.lang.reflect.Field;
import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class StevesCartsModule {

	public static Class  TileEntityCartAssembler = null;
	public static Class  TileEntityCargo = null;
	public static Class  MinecartModular = null;
	public static Class  StevesItems = null;
	public static Field  ItemCartComponent = null;
	public static Field  ItemCartModule = null;

	public static void register(){
		try{
			Class ModStevesCarts = Class.forName("vswe.stevescarts.StevesCarts");
			Waila.log.log(Level.INFO, "StevesCarts mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[StevesCarts] StevesCarts mod not found.");
			return;
		}			
		
		try{
			TileEntityCartAssembler = Class.forName("vswe.stevescarts.TileEntities.TileEntityCartAssembler");
			TileEntityCargo = Class.forName("vswe.stevescarts.TileEntities.TileEntityCargo");
			MinecartModular = Class.forName("vswe.stevescarts.Carts.MinecartModular");
			StevesItems = Class.forName("vswe.stevescarts.Items.Items");
			
			ItemCartComponent = StevesItems.getDeclaredField("component");
			ItemCartModule    = StevesItems.getDeclaredField("modules");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[StevesCarts] Class not found. " + e);
			return;
		} catch (NoSuchFieldException e) {
			Waila.log.log(Level.WARNING, "[StevesCarts] Field not found. " + e);
			return;
		} catch (Exception e) {
			Waila.log.log(Level.WARNING, "[StevesCarts] Error while loading module. " + e);
			return;
		}			
			
		ModuleRegistrar.instance().registerBodyProvider(new HUDCartAssembler(),    TileEntityCartAssembler);
		ModuleRegistrar.instance().registerBodyProvider(new HUDCargoManager(),    TileEntityCargo);		
		ModuleRegistrar.instance().registerHeadProvider(new HUDMinecartModular(),  MinecartModular);
		ModuleRegistrar.instance().registerBodyProvider(new HUDMinecartModular(),  MinecartModular);
		
		ModuleRegistrar.instance().registerSyncedNBTKey("*", TileEntityCartAssembler);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", MinecartModular);		
	}	
	
}
