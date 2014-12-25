package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThermalExpansionModule {

	public static Class  IEnergyProvider = null;
	public static Method IEnergyProvider_getMaxStorage = null;
	public static Method IEnergyProvider_getCurStorage = null;

	public static Class  IEnergyReceiver = null;
	public static Method IEnergyReceiver_getMaxStorage = null;
	public static Method IEnergyReceiver_getCurStorage = null;	

	public static Class  IEnergyInfo = null;
	public static Method IEnergyInfo_getMaxStorage = null;
	public static Method IEnergyInfo_getCurStorage = null;	
	
	public static Class TileEnergyCell = null;
	public static Field TileEnergyCell_Recv = null;
	public static Field TileEnergyCell_Send = null;
	
	public static Class  TileTank = null;
	public static Method TileTank_getTankFluid = null;
	public static Method TileTank_getTankCapacity = null;
	public static Method TileTank_getTankAmount = null;
	public static Field  TileTank_mode = null;
	
	public static Class TileTesseract = null;
	public static Field TileTesseract_Item   = null;
	public static Field TileTesseract_Fluid  = null;
	public static Field TileTesseract_Energy = null;
	
	public static Class  ISecureTile = null;
	public static Method ISecureTile_getAccess    = null;
	public static Method ISecureTile_getOwnerName = null;
	
	public static Class BlockMultipart = null;
	
	public static Class  IBlockInfo = null;
	public static Method IBlockInfo_getBlockInfo = null;
	
	public static void register(){
		// XXX : We register the Energy interface first
		try{
			IEnergyProvider              = Class.forName("cofh.api.energy.IEnergyProvider");
			IEnergyProvider_getMaxStorage = IEnergyProvider.getMethod("getMaxEnergyStored", ForgeDirection.class);
			IEnergyProvider_getCurStorage = IEnergyProvider.getMethod("getEnergyStored",    ForgeDirection.class);
			
			IEnergyReceiver              = Class.forName("cofh.api.energy.IEnergyReceiver");
			IEnergyReceiver_getMaxStorage = IEnergyReceiver.getMethod("getMaxEnergyStored", ForgeDirection.class);
			IEnergyReceiver_getCurStorage = IEnergyReceiver.getMethod("getEnergyStored",    ForgeDirection.class);			
			
			IEnergyInfo                  = Class.forName("cofh.api.tileentity.IEnergyInfo");
			IEnergyInfo_getMaxStorage    = IEnergyInfo.getMethod("getInfoMaxEnergyStored");
			IEnergyInfo_getCurStorage    = IEnergyInfo.getMethod("getInfoEnergyStored");			

			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyProvider);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIEnergyHandler(), IEnergyProvider);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyReceiver);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIEnergyHandler(), IEnergyReceiver);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIEnergyHandler(), IEnergyInfo);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIEnergyHandler(), IEnergyInfo);			
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Energy hooks." + e);
		}	

		// XXX : We register the energy cell
		try{		
			TileEnergyCell = Class.forName("thermalexpansion.block.cell.TileCell");
			TileEnergyCell_Recv = TileEnergyCell.getDeclaredField("energyReceive");
			TileEnergyCell_Send = TileEnergyCell.getDeclaredField("energySend");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energycell");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEnergyCell(), TileEnergyCell);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerEnergyCell(), TileEnergyCell);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Energy Cell hooks." + e);
		}				
			
		// XXX : We register the Tank interface
		try{
			TileTank                 = Class.forName("thermalexpansion.block.tank.TileTank");
			TileTank_getTankFluid    = TileTank.getMethod("getTankFluid");
			TileTank_getTankCapacity = TileTank.getMethod("getTankCapacity");
			TileTank_getTankAmount   = TileTank.getMethod("getTankFluidAmount");
			TileTank_mode            = TileTank.getField("mode");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidtype");
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.fluidamount");
			ModuleRegistrar.instance().addConfig("Thermal Expansion", "thermalexpansion.tankmode");
			ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(), TileTank);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(), TileTank);		
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTank(), TileTank);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Tank hooks." + e);
		}			
	
		// XXX : We register the Tesseract interface
		try{
			TileTesseract = Class.forName("thermalexpansion.block.ender.TileTesseract");
			TileTesseract_Item   = TileTesseract.getDeclaredField("modeItem");
			TileTesseract_Fluid  = TileTesseract.getDeclaredField("modeFluid");
			TileTesseract_Energy = TileTesseract.getDeclaredField("modeEnergy");			
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tesssendrecv");
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.tessfreq");
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTesseract(), TileTesseract);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading Tesseract hooks." + e);
		}		

		// XXX : We register the ISecureTile interface
		try{
			ISecureTile   = Class.forName("cofh.api.tileentity.ISecurable");
			ISecureTile_getAccess    = ISecureTile.getDeclaredMethod("getAccess");
			ISecureTile_getOwnerName = ISecureTile.getDeclaredMethod("getOwnerName");
			
			ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.owner");		
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerISecureTile(), ISecureTile);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerISecureTile(), ISecureTile);
			//ModuleRegistrar.instance().registerSyncedNBTKey("Owner", ISecureTile);
			//ModuleRegistrar.instance().registerSyncedNBTKey("Access", ISecureTile);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading ISecureTile hooks." + e);
		}			
		
		// XXX : We register the IBlockInfo interface
		/*
		try{
			IBlockInfo              = Class.forName("cofh.api.block.IBlockInfo");
			IBlockInfo_getBlockInfo = IBlockInfo.getMethod("getBlockInfo", IBlockAccess.class, int.class, int.class, int.class, ForgeDirection.class, EntityPlayer.class, List.class, boolean.class);
			
			
			//ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIBlockInfo(), IBlockInfo);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIBlockInfo(), IBlockInfo);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading IBlockInfo hooks." + e);
		}			
		*/
	}
	
	
}
