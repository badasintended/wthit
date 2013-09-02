package mcp.mobius.waila.addons.appeng;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class AppEngModule {

	public static Class  TileStorageMonitor = null;
	public static Class  IAEItemStack       = null;
	public static Method TileStorageMonitor_getItem = null;
	public static Method IAEItemStack_getItemStack  = null;
	
	public static void register(){
		try{
			TileStorageMonitor = Class.forName("appeng.me.tile.TileStorageMonitor");
			IAEItemStack       = Class.forName("appeng.api.IAEItemStack");
			
			
			TileStorageMonitor_getItem = TileStorageMonitor.getMethod("getItem");
			IAEItemStack_getItemStack  = IAEItemStack.getMethod("getItemStack");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[AppEng] Class not found. " + e);
			return;
		} catch (SecurityException e) {
			mod_Waila.log.log(Level.WARNING, "[AppEng] Security Exception. " + e);
			return;
		} catch (NoSuchMethodException e) {
			mod_Waila.log.log(Level.WARNING, "[AppEng] Method not found. " + e);
			return;
		}
		
		mod_Waila.log.log(Level.INFO, "AppEng mod found.");
		ExternalModulesHandler.instance().addConfig("Applied Energetic", "appeng.monitorcontent", "Monitor's content");		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDAppEngMonitor(), TileStorageMonitor);		
	}
}
