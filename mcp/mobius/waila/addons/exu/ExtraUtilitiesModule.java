package mcp.mobius.waila.addons.exu;

import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ExtraUtilitiesModule {

	public static Class ModExtraUtilities = null;
	public static Class TileEntityDrum    = null;	
	
	public static void register(){
		

		try{
			Class ModExtraUtilities = Class.forName("extrautils.ExtraUtils");
			Waila.log.log(Level.INFO, "ExtraUtilities mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[ExtraUtilities] ExtraUtilities mod not found.");
			return;
		}			
		
		try{
			TileEntityDrum = Class.forName("extrautils.tileentity.TileEntityDrum");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[ExtraUtilities] Class not found. " + e);
			return;
		}
		
		ModuleRegistrar.instance().addConfigRemote("ExtraUtilities", "extrautilities.fluidamount");		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerDrum(),  TileEntityDrum);			
	}
}
