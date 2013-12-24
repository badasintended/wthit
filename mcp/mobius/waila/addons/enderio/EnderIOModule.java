package mcp.mobius.waila.addons.enderio;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class EnderIOModule {

	public static Class TileCapacitorBank = null;
	public static Method TCB_getMaxInput     = null;
	public static Method TCB_getMaxOutput    = null;
	public static Method TCB_getEnergyStored = null;
	public static Method TCB_getMaxEnergyStored = null;
	public static Method TCB_getMaxIO = null;

	public static Class TileTesseract = null;	
	
	public static void register(){	
		try{
			Class ModClass = Class.forName("crazypants.enderio.EnderIO");
			mod_Waila.log.log(Level.INFO, "EnderIO mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[EnderIO] EnderIO mod not found.");
			return;
		}
		
		try{
			
			TileCapacitorBank = Class.forName("crazypants.enderio.machine.power.TileCapacitorBank");
			TCB_getEnergyStored    = TileCapacitorBank.getMethod("getEnergyStored");
			TCB_getMaxInput        = TileCapacitorBank.getMethod("getMaxInput");
			TCB_getMaxOutput       = TileCapacitorBank.getMethod("getMaxOutput");
			TCB_getMaxEnergyStored = TileCapacitorBank.getMethod("getMaxEnergyStored");
			TCB_getMaxIO           = TileCapacitorBank.getMethod("getMaxIO");
			
			TileTesseract = Class.forName("crazypants.enderio.machine.hypercube.TileHyperCube");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
//			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ModuleRegistrar.instance().addConfig("EnderIO", "enderio.inout");
		ModuleRegistrar.instance().addConfig("EnderIO", "enderio.storage");
		ModuleRegistrar.instance().addConfig("EnderIO", "enderio.owner");
		ModuleRegistrar.instance().addConfig("EnderIO", "enderio.channel");		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerCapacitor(), TileCapacitorBank);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
	}	
	
}
