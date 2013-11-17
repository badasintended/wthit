package mcp.mobius.waila.addons.enderstorage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class EnderStorageModule {

	public static Class TileFrequencyOwner = null;
	public static Field TileFrequencyOwner_Freq = null;
	
	public static Class EnderStorageManager = null;	
	public static Method GetColourFromFreq = null;
	
	public static Class TileEnderTank = null;
	
	public static void register(){	
		try{
			Class EnderStorage = Class.forName("codechicken.enderstorage.EnderStorage");
			mod_Waila.log.log(Level.INFO, "EnderStorage mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[EnderStorage] EnderStorage mod not found.");
			return;
		}
		
		try{
			
			TileFrequencyOwner  = Class.forName("codechicken.enderstorage.common.TileFrequencyOwner");		
			TileFrequencyOwner_Freq = TileFrequencyOwner.getField("freq");
			
			EnderStorageManager = Class.forName("codechicken.enderstorage.api.EnderStorageManager");
			GetColourFromFreq   = EnderStorageManager.getDeclaredMethod("getColourFromFreq", Integer.TYPE, Integer.TYPE);
			
			TileEnderTank = Class.forName("codechicken.enderstorage.storage.liquid.TileEnderTank");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ExternalModulesHandler.instance().addConfig("EnderStorage", "enderstorage.colors");		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerStorage(),    TileFrequencyOwner);
	}
}
