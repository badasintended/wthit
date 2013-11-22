package mcp.mobius.waila.addons.betterbarrels;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import codechicken.lib.lang.LangUtil;
import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;

public class BetterBarrelsModule {
	public static Class  TileEntityBarrel         = null;
	public static Class  IBarrelStorage           = null;
	public static Field  TileEntityBarrel_Storage = null;
	public static Method IBarrelStorage_getItem   = null;

	public static void register(){
		try{
			TileEntityBarrel = Class.forName("mcp.mobius.betterbarrels.common.TileEntityBarrel");
			IBarrelStorage   = Class.forName("mcp.mobius.betterbarrels.common.IBarrelStorage");
			TileEntityBarrel_Storage = TileEntityBarrel.getField("storage");
			IBarrelStorage_getItem   = IBarrelStorage.getMethod("getItem");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[BB] BetterBarrel classes not found.");
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[BB] Storage field not found.");			
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[BB] getItem() not found.");
		}
		
		if (IBarrelStorage_getItem != null){
			mod_Waila.log.log(Level.INFO, "Waila module BetterBarrel succefully hooked.");
			ExternalModulesHandler.instance().addConfig("BetterBarrels", "bb.content");			
			ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBBContent(), TileEntityBarrel);			
		}
	}	
	
}
