package mcp.mobius.waila.addons.statues;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class StatuesModule {

	public static Class TileEntityStatue  = null;
	public static Field skinName = null;
	
	public static void register(){
		try{
			Class Statues = Class.forName("info.jbcs.minecraft.statues.Statues");
			Waila.log.log(Level.INFO, "Statues mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[Statues] Statues mod not found.");
			return;
		}			
		
		try{
			TileEntityStatue = Class.forName("info.jbcs.minecraft.statues.TileEntityStatue");
			skinName = TileEntityStatue.getDeclaredField("skinName");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARN, "[Statues] Class not found. " + e);
			return;
		} catch (NoSuchFieldException e){
			Waila.log.log(Level.WARN, "[Statues] Class not found. " + e);
			return;
		}
		
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerStatues(),  TileEntityStatue);
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerStatues(),  TileEntityStatue);		
		ModuleRegistrar.instance().registerSyncedNBTKey("*", TileEntityStatue);
	}	
	
}
