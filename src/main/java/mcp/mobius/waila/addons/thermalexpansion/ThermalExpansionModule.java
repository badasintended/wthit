package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThermalExpansionModule {

	public static Class  IEnergyHandler = null;
	public static Method IEnergyHandler_getMaxStorage = null;
	
	public static Class TileEnergyCell = null;
	
	public static Class  TileTank = null;
	public static Method TileTank_getTankFluid = null;
	public static Method TileTank_getTankCapacity = null;
	public static Field  TileTank_mode = null;

	//public static Class  TileConduitFluid = null;
	//public static Method TileConduitFluid_getRenderFluid = null;
	//public static Method TileConduitFluid_getRenderFluidLevel = null;
	
	//public static Class  TileConduitItems = null;
	
	public static Class  TileTesseract = null;
	
	public static Class  ISecureTile = null;
	
	public static Class BlockMultipart = null;
	
	//public static Field  TileTesseract_modItem   = null;
	//public static Field  TileTesseract_modFluid  = null;
	//public static Field  TileTesseract_modEnergy = null;	
	
	
	//public static Class  TileConduitBase = null;
	//public static Field  TileConduitBase_isInput  = null;
	//public static Field  TileConduitBase_isOutput = null;	
	
	public static void register(){
		/*
		try{
			Class ModThermalExpansion = Class.forName("thermalexpansion.ThermalExpansion");
			Waila.log.log(Level.INFO, "Thermal Expansion mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[Thermal Expansion] ThermalExpansion mod not found.");
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

			//TileConduitFluid = Class.forName("thermalexpansion.block.conduit.fluid.TileConduitFluid");			
			//TileConduitFluid_getRenderFluid      = TileConduitFluid.getMethod("getRenderFluid");
			//TileConduitFluid_getRenderFluidLevel = TileConduitFluid.getMethod("getRenderFluidLevel");
			//TileConduitItems = Class.forName("thermalexpansion.block.conduit.item.TileConduitItem");				
			
			TileTesseract = Class.forName("thermalexpansion.block.tesseract.TileTesseract");
			
			ISecureTile   = Class.forName("cofh.api.tileentity.ISecureTile");
			
			BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
			
			//TileTesseract_modItem   = TileTesseract.getField("modItem");
			//TileTesseract_modFluid  = TileTesseract.getField("modFluid");
			//TileTesseract_modEnergy = TileTesseract.getField("modEnergy");			
			
			//TileConduitBase = Class.forName("thermalexpansion.block.conduit.TileConduitBase");
			//TileConduitBase_isInput  = TileConduitBase.getField("isInput");
			//TileConduitBase_isOutput = TileConduitBase.getField("isOutput");			
			
			//TileEnergyCell_energyReceive = TileEnergyCell.getField("energyReceive");
			//TileEnergyCell_energySend = TileEnergyCell.getField("energySend");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[Thermal Expansion] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			Waila.log.log(Level.WARNING, "[Thermal Expansion] Method not found." + e);
			return;			
		} catch (NoSuchFieldException e){
			Waila.log.log(Level.WARNING, "[Thermal Expansion] Field not found." + e);
			return;			
//		} catch (Exception e){
//			mod_Waila.log.log(Level.WARNING, "[Thermal Expansion] Unhandled exception." + e);
//			return;			
		}		
		
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energycell");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidtype");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidamount");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.conditemmode");		
		ModuleRegistrar.instance().addConfig("Thermal Expansion", "thermalexpansion.tankmode");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tesssendrecv");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tessfreq");
		ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.owner");		
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyHandler);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEnergyCell(), TileEnergyCell);
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(), TileTank);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(), TileTank);
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerISecureTile(), ISecureTile);

		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerConduitFluid(), BlockMultipart);		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerConduitItem(),  BlockMultipart);		
			
		ModuleRegistrar.instance().registerSyncedNBTKey("*", IEnergyHandler);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", TileEnergyCell);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", TileTank);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", TileTesseract);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", ISecureTile);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", BlockMultipart);
		*/
	}
	
	
}
