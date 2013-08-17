package mcp.mobius.waila.addons.twilightforest;

import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class TwilightForestModule {

	public static Class BlockTFRoots = null;	
	public static Class BlockTFPlant = null;
	public static Class BlockTFSapling = null;
	
	public static void register(){

		try{
			Class TwilightForestMod = Class.forName("twilightforest.TwilightForestMod");
			mod_Waila.log.log(Level.INFO, "TwilightForestMod mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[TwilightForestMod] TwilightForestMod mod not found.");
			return;
		}		

		try {
			BlockTFRoots = Class.forName("twilightforest.block.BlockTFRoots");
			BlockTFPlant = Class.forName("twilightforest.block.BlockTFPlant");
			BlockTFSapling = Class.forName("twilightforest.block.BlockTFSapling");				
		} catch (ClassNotFoundException e) {
			mod_Waila.log.log(Level.WARNING, "[TwilightForestMod] Class not found. " + e);
		}

		
		ExternalModulesHandler.instance().registerStackProvider(new TwilightForestGenericOverride(), BlockTFRoots);
		ExternalModulesHandler.instance().registerStackProvider(new TwilightForestGenericOverride(), BlockTFPlant);		
		//ExternalModulesHandler.instance().registerStackProvider(new TwilightForestGenericOverride(), BlockTFSapling);				
	}

}
