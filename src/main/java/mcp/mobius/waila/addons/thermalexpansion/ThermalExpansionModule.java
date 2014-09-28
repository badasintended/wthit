package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

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
	
	public static Class  TileTesseract = null;
	public static Class  ISecureTile = null;
	public static Class BlockMultipart = null;
	
	public static void register(){
		// XXX : We register the Energy interface first
		try{
			IEnergyHandler               = Class.forName("cofh.api.energy.IEnergyHandler");
			IEnergyHandler_getMaxStorage = IEnergyHandler.getMethod("getMaxEnergyStored", ForgeDirection.class);
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyHandler);			
			ModuleRegistrar.instance().registerSyncedNBTKey("*", IEnergyHandler);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Energy hooks." + e);
		}	

		// XXX : We register the energy cell
		try{		
			TileEnergyCell = Class.forName("thermalexpansion.block.cell.TileCell");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energycell");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEnergyCell(), TileEnergyCell);
			ModuleRegistrar.instance().registerSyncedNBTKey("*", TileEnergyCell);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Energy Cell hooks." + e);
		}				
			
		// XXX : We register the Tank interface
		try{
			TileTank                 = Class.forName("thermalexpansion.block.tank.TileTank");
			TileTank_getTankFluid    = TileTank.getMethod("getTankFluid");
			TileTank_getTankCapacity = TileTank.getMethod("getTankCapacity");
			TileTank_mode            = TileTank.getField("mode");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidtype");
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidamount");
			ModuleRegistrar.instance().addConfig("Thermal Expansion", "thermalexpansion.tankmode");
			ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(), TileTank);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(), TileTank);			
			ModuleRegistrar.instance().registerSyncedNBTKey("*", TileTank);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Tank hooks." + e);
		}			
	
		// XXX : We register the Tesseract interface
		try{
			TileTesseract = Class.forName("thermalexpansion.block.ender.TileTesseract");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tesssendrecv");
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tessfreq");
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
			ModuleRegistrar.instance().registerSyncedNBTKey("*", TileTesseract);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Tesseract hooks." + e);
		}		

		// XXX : We register the ISecureTile interface
		try{
			ISecureTile   = Class.forName("cofh.api.tileentity.ISecurable");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.owner");		
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerISecureTile(), ISecureTile);
			ModuleRegistrar.instance().registerSyncedNBTKey("*", ISecureTile);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading ISecureTile hooks." + e);
		}			
	}
	
	
}
