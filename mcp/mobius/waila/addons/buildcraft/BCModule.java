package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import net.minecraftforge.common.ForgeDirection;

public class BCModule {

	public static Class  TileTank       = null;
	public static Class  IPowerReceptor = null;
	public static Class  PipeTransportPower = null;
	public static Class  TileGenericPipe    = null;
	public static Class  TileEngine         = null;
	
	public static Method TileTank_getTankInfo      = null;
	//public static Method IPowerReceptor_PowerRequest = null;

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
			IPowerReceptor      = Class.forName("buildcraft.api.power.IPowerReceptor");
			PipeTransportPower  = Class.forName("buildcraft.transport.PipeTransportPower");
			TileGenericPipe     = Class.forName("buildcraft.transport.TileGenericPipe");
			TileEngine          = Class.forName("buildcraft.energy.TileEngine");
			
			TileTank_getTankInfo      = TileTank.getMethod("getTankInfo", ForgeDirection.class);
			//IPowerReceptor_PowerRequest = IPowerReceptor.getMethod("powerRequest", ForgeDirection.class);
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[BC] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[BC] Method not found." + e);
			return;	
		}
		
		ExternalModulesHandler.instance().addConfig("Buildcraft", "bc.tankamount", "Liquid amount");
		ExternalModulesHandler.instance().addConfig("Buildcraft", "bc.tanktype",   "Liquid type");
		//ExternalModulesHandler.instance().addConfigRemote("Buildcraft", "bc.powerpipe",   "Power pipes");
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerBCTanks(), TileTank);			
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBCTanks(), TileTank);
		//ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBCPipes(), TileGenericPipe);
		//ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBCEngine(), TileEngine);			
		//ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerBCIPowerReceptor(), PipeTransportPower);
	}
	
}
