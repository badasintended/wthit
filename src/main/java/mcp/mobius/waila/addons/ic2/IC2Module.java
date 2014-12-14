package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class IC2Module {

	public static Class TileBaseGenerator = null;
	public static Field TileBaseGenerator_storage    = null;
	public static Field TileBaseGenerator_maxStorage = null;
	public static Field TileBaseGenerator_production = null;
	
	public static Class TileGeoGenerator = null;
	public static Field TileGeoGenerator_storage    = null;
	public static Field TileGeoGenerator_maxStorage = null;
	public static Field TileGeoGenerator_production = null;

	public static Class TileKineticGenerator = null;
	public static Field TileKineticGenerator_storage    = null;
	public static Field TileKineticGenerator_maxStorage = null;
	public static Field TileKineticGenerator_production = null;	
	
	public static Class TileSemifluidGenerator = null;
	public static Field TileSemifluidGenerator_storage    = null;
	public static Field TileSemifluidGenerator_maxStorage = null;
	public static Field TileSemifluidGenerator_production = null;	
	
	public static Class TileStirlingGenerator = null;
	public static Field TileStirlingGenerator_storage    = null;
	public static Field TileStirlingGenerator_maxStorage = null;
	public static Field TileStirlingGenerator_production = null;	
	
	public static void register(){
		// XXX : We register the Energy interface first
		try{
			TileBaseGenerator            = Class.forName("ic2.core.block.generator.tileentity.TileEntityBaseGenerator");
			TileBaseGenerator_storage    = TileBaseGenerator.getDeclaredField("storage");
			TileBaseGenerator_maxStorage = TileBaseGenerator.getDeclaredField("maxStorage");
			TileBaseGenerator_production = TileBaseGenerator.getDeclaredField("production");

			TileGeoGenerator            = Class.forName("ic2.core.block.generator.tileentity.TileEntityGeoGenerator");
			TileGeoGenerator_storage    = TileGeoGenerator.getDeclaredField("storage");
			TileGeoGenerator_maxStorage = TileGeoGenerator.getDeclaredField("maxStorage");
			TileGeoGenerator_production = TileGeoGenerator.getDeclaredField("production");			
			
			TileKineticGenerator            = Class.forName("ic2.core.block.generator.tileentity.TileEntityKineticGenerator");
			TileKineticGenerator_storage    = TileKineticGenerator.getDeclaredField("EUstorage");
			TileKineticGenerator_maxStorage = TileKineticGenerator.getDeclaredField("maxEUStorage");
			TileKineticGenerator_production = TileKineticGenerator.getDeclaredField("production");		
			TileKineticGenerator_storage.setAccessible(true);
			TileKineticGenerator_maxStorage.setAccessible(true);
			TileKineticGenerator_production.setAccessible(true);
			
			TileSemifluidGenerator            = Class.forName("ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator");
			TileSemifluidGenerator_storage    = TileSemifluidGenerator.getDeclaredField("storage");
			TileSemifluidGenerator_maxStorage = TileSemifluidGenerator.getDeclaredField("maxStorage");
			TileSemifluidGenerator_production = TileSemifluidGenerator.getDeclaredField("production");
			TileSemifluidGenerator_storage.setAccessible(true);
			TileSemifluidGenerator_maxStorage.setAccessible(true);
			TileSemifluidGenerator_production.setAccessible(true);
			
			TileStirlingGenerator            = Class.forName("ic2.core.block.generator.tileentity.TileEntityStirlingGenerator");
			TileStirlingGenerator_storage    = TileStirlingGenerator.getDeclaredField("EUstorage");
			TileStirlingGenerator_maxStorage = TileStirlingGenerator.getDeclaredField("maxEUStorage");
			TileStirlingGenerator_production = TileStirlingGenerator.getDeclaredField("production");			
			
			//ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileBaseGenerator);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileGeoGenerator);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileKineticGenerator);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileSemifluidGenerator);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTEGenerator(), TileStirlingGenerator);
			
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTEGenerator(), TileBaseGenerator);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTEGenerator(), TileGeoGenerator);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTEGenerator(), TileKineticGenerator);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTEGenerator(), TileSemifluidGenerator);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerTEGenerator(), TileStirlingGenerator);
			
			ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.storage");
			ModuleRegistrar.instance().addConfigRemote("IndustrialCraft2", "ic2.outputeu");			
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[IndustrialCraft 2] Error while loading TileEntityBaseGenerator hooks." + e);
		}		
		
	}
	
}
