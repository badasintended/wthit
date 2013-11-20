package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.ForgeDirection;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class ThermalExpansionModule {

	public static Class  IEnergyHandler = null;
	public static Method IEnergyHandler_getMaxStorage = null;
	
	public static Class TileEnergyCell = null;
	
	public static Class  TileTank = null;
	public static Method TileTank_getTankFluid = null;
	public static Method TileTank_getTankCapacity = null;
	public static Field  TileTank_mode = null;

	public static Class  TileConduitFluid = null;
	public static Method TileConduitFluid_getRenderFluid = null;
	public static Method TileConduitFluid_getRenderFluidLevel = null;
	
	public static Class  TileConduitItems = null;
	
	public static Class  TileTesseract = null;
	
	public static Class  ISecureTile = null;
	
	//public static Field  TileTesseract_modItem   = null;
	//public static Field  TileTesseract_modFluid  = null;
	//public static Field  TileTesseract_modEnergy = null;	
	
	
	//public static Class  TileConduitBase = null;
	//public static Field  TileConduitBase_isInput  = null;
	//public static Field  TileConduitBase_isOutput = null;	
	
	public static void register(){

		try{
			Class ModThermalExpansion = Class.forName("thermalexpansion.ThermalExpansion");
			mod_Waila.log.log(Level.INFO, "Thermal Expansion mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[Thermal Expansion] ThermalExpansion mod not found.");
			return;
		}		
		
		try{
			IEnergyHandler = Class.forName("cofh.api.energy.IEnergyHandler");
			IEnergyHandler_getMaxStorage = IEnergyHandler.getMethod("getMaxEnergyStored", ForgeDirection.class);
			TileEnergyCell = Class.forName("thermalexpansion.block.energycell.TileEnergyCell");
			
			TileTank       = Class.forName("thermalexpansion.block.tank.TileTank");
			TileTank_getTankFluid    = TileTank.getMethod("getTankFluid");
			TileTank_getTankCapacity = TileTank.getMethod("getTankCapacity");
			TileTank_mode            = TileTank.getField("mode");

			TileConduitFluid = Class.forName("thermalexpansion.block.conduit.fluid.TileConduitFluid");			
			TileConduitFluid_getRenderFluid      = TileConduitFluid.getMethod("getRenderFluid");
			TileConduitFluid_getRenderFluidLevel = TileConduitFluid.getMethod("getRenderFluidLevel");

			TileConduitItems = Class.forName("thermalexpansion.block.conduit.item.TileConduitItem");				
			
			TileTesseract = Class.forName("thermalexpansion.block.tesseract.TileTesseract");
			
			ISecureTile   = Class.forName("cofh.api.tileentity.ISecureTile");
			
			//TileTesseract_modItem   = TileTesseract.getField("modItem");
			//TileTesseract_modFluid  = TileTesseract.getField("modFluid");
			//TileTesseract_modEnergy = TileTesseract.getField("modEnergy");			
			
			//TileConduitBase = Class.forName("thermalexpansion.block.conduit.TileConduitBase");
			//TileConduitBase_isInput  = TileConduitBase.getField("isInput");
			//TileConduitBase_isOutput = TileConduitBase.getField("isOutput");			
			
			//TileEnergyCell_energyReceive = TileEnergyCell.getField("energyReceive");
			//TileEnergyCell_energySend = TileEnergyCell.getField("energySend");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Method not found." + e);
			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Field not found." + e);
			return;			
//		} catch (Exception e){
//			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Unhandled exception." + e);
//			return;			
		}		
		
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energycell");
		ExternalModulesHandler.instance().addConfig("Thermal Expansion", "thermalexpansion.fluidtype");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidamount");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.conditemmode");		
		ExternalModulesHandler.instance().addConfig("Thermal Expansion", "thermalexpansion.tankmode");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tesssendrecv");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tessfreq");
		ExternalModulesHandler.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.owner");		
		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyHandler);
		//ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), TileEnergyCell);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerEnergyCell(), TileEnergyCell);
		ExternalModulesHandler.instance().registerHeadProvider(new HUDHandlerTank(), TileTank);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTank(), TileTank);
		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerConduitFluid(), TileConduitFluid);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerConduitItem(), TileConduitItems);		
		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerISecureTile(), ISecureTile);
		
		//ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerConduitBase(), TileConduitBase);				
		
	}
	
	
}
