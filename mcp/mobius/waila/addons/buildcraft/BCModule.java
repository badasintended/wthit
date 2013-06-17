package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2IEnergySink;
import net.minecraftforge.common.ForgeDirection;

public class BCModule {

	public static Class  TileTank = null;
	public static Method TileTank_GetTanks = null;

	public static void register(){
		try {
			Class ModBuildcraftFactory = Class.forName("buildcraft.BuildCraftFactory");
			mod_Waila.log.log(Level.INFO, "Buildcraft|Factory mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "Buildcraft|Factory mod not found. Skipping.");	
			return;
		}		
		
		try{
			TileTank            = Class.forName("buildcraft.factory.TileTank");
			TileTank_GetTanks   = TileTank.getMethod("getTanks", ForgeDirection.class);
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[BC] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[BC] Method not found." + e);
			return;	
		}	

		ConfigHandler.instance().addConfig("Buildcraft", "bc.tankamount", "Liquid amount");
		ConfigHandler.instance().addConfig("Buildcraft", "bc.tanktype",   "Liquid type");
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerBCTanks(), TileTank);			
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBCTanks(), TileTank);		
	}
	
}
