package mcp.mobius.waila.addons.magicalcrops;

import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class MagicalCropsModule {
	public static Class BlockMagicalCrops  = null;

	
	public static void register(){
		try{
			Class MagicalCrops = Class.forName("magicalcrops.mod_sCrops");
			Waila.log.log(Level.INFO, "MagicalCrops mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[MagicalCrops] MagicalCrops mod not found.");
			return;
		}			
		
		try{
			BlockMagicalCrops = Class.forName("magicalcrops.BlockMagicalCrops");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[MagicalCrops] Class not found. " + e);
			return;
		}
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerMagicalCrops(),  BlockMagicalCrops);
	}
}
