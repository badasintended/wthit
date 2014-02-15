package mcp.mobius.waila.addons.buildcraft;

import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraftforge.common.ForgeDirection;

public class BCModule {

	public static Class  TileTank       = null;
	public static Class  IPowerReceptor = null;
	public static Class  PipeTransportPower = null;
	public static Class  TileGenericPipe    = null;
	public static Class  TileEngine         = null;
	
	public static Method TileTank_getTankInfo      = null;

	public static void register(){
		try {
			Class ModBuildcraftFactory = Class.forName("buildcraft.BuildCraftFactory");
			Waila.log.log(Level.INFO, "Buildcraft|Factory mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "Buildcraft|Factory mod not found. Skipping.");	
			return;
		}		
		
		try{
			TileTank            = Class.forName("buildcraft.factory.TileTank");
			TileTank_getTankInfo      = TileTank.getMethod("getTankInfo", ForgeDirection.class);
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[BC] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			Waila.log.log(Level.WARNING, "[BC] Method not found." + e);
			return;	
		}
		
		ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tankamount");
		ModuleRegistrar.instance().addConfig("Buildcraft", "bc.tanktype");
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBCTanks(), TileTank);			
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBCTanks(), TileTank);
	}
	
}
