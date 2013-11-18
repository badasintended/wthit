package mcp.mobius.waila.addons.etb;

import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.item.Item;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class ETBModule {

	public static Class TileSocket      = null;
	public static Field Socket_sides    = null;
	public static Field Socket_configs  = null;
	//public static Field Socket_sideID   = null;
	//public static Field Socket_sideMeta = null;
	//public static Field Socket_facID    = null;
	//public static Field Socket_facMeta  = null;	
	
	public static Class SocketsMod = null;
	public static Item  module     = null;
	
	public static Class SideConfig   = null;
	public static Field SC_tank      = null;
	public static Field SC_inventory = null;
	public static Field SC_rsControl = null;	
	public static Field SC_rsLatch   = null;		
	
	public static void register(){	
		try{
			Class ModClass = Class.forName("emasher.sockets.TileSocket");
			mod_Waila.log.log(Level.INFO, "Engineer Toolbox mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Engineer Toolbox] Engineer Toolbox mod not found.");
			return;
		}	
		
		try{
			
			TileSocket     = Class.forName("emasher.sockets.TileSocket");		
			Socket_sides   = TileSocket.getField("sides");
			Socket_configs = TileSocket.getField("configs");
			//Socket_sideID = TileSocket.getField("sideID");
			//Socket_sideMeta = TileSocket.getField("sideMeta");
			//Socket_facID = TileSocket.getField("facID");
			//Socket_facMeta = TileSocket.getField("facMeta");
		
			SocketsMod = Class.forName("emasher.sockets.SocketsMod");
			module     = (Item)SocketsMod.getField("module").get(null);
			
			SideConfig   = Class.forName("emasher.api.SideConfig");
			SC_tank      = SideConfig.getField("tank");
			SC_inventory = SideConfig.getField("inventory");
			SC_rsControl = SideConfig.getField("rsControl");
			SC_rsLatch   = SideConfig.getField("rsLatch");			
			
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
//		} catch (NoSuchMethodException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
//			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}		
		
		ExternalModulesHandler.instance().addConfig("Engineer Toolbox", "etb.displaydata");
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerSocket(), TileSocket);
		
	}
}
