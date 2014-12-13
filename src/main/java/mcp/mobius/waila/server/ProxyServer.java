package mcp.mobius.waila.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import cpw.mods.fml.common.Loader;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.agriculture.AgricultureModule;
import mcp.mobius.waila.addons.carpenters.CarpentersModule;
import mcp.mobius.waila.addons.enderio.EnderIOModule;
import mcp.mobius.waila.addons.enderstorage.EnderStorageModule;
import mcp.mobius.waila.addons.etb.ETBModule;
import mcp.mobius.waila.addons.exu.ExtraUtilitiesModule;
import mcp.mobius.waila.addons.gravestone.GravestoneModule;
import mcp.mobius.waila.addons.harvestcraft.HarvestcraftModule;
import mcp.mobius.waila.addons.magicalcrops.MagicalCropsModule;
import mcp.mobius.waila.addons.openblocks.OpenBlocksModule;
import mcp.mobius.waila.addons.projectred.ProjectRedModule;
import mcp.mobius.waila.addons.railcraft.RailcraftModule;
import mcp.mobius.waila.addons.statues.StatuesModule;
import mcp.mobius.waila.addons.stevescarts.StevesCartsModule;
import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;
import mcp.mobius.waila.addons.thermalexpansion.ThermalExpansionModule;
import mcp.mobius.waila.addons.twilightforest.TwilightForestModule;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.handlers.DecoratorFMP;
import mcp.mobius.waila.handlers.HUDHandlerFMP;

public class ProxyServer {

	public ProxyServer() {}

	public void registerHandlers(){}	
	
	public void registerMods(){
		
		HUDHandlerVanilla.register();
		
		/* BUILDCRAFT */
		//BCModule.register();
		
		/* INDUSTRIALCRAFT2 */
		//IC2Module.register();
		
		/*Thaumcraft*/
		ThaumcraftModule.register();
		
		/*EnderStorage*/
		EnderStorageModule.register();
		
		/*Gravestone*/
		GravestoneModule.register();
		
		/*Twilight forest*/
		TwilightForestModule.register();
		
		/* Thermal Expansion */
		ThermalExpansionModule.register();
		
		/* ETB */
		ETBModule.register();
		
		/* EnderIO */
		EnderIOModule.register();		
		
		/* Buildcraft Power API */
		//BCPowerAPIModule.register();
		
		/* ProjectRed API */
		ProjectRedModule.register();
		
		/* ExtraUtilities */
		ExtraUtilitiesModule.register();	
		
		/* OpenBlocks */
		OpenBlocksModule.register();
		
		/* Railcraft */
		RailcraftModule.register();		
		
		/* Steve's Carts */
		StevesCartsModule.register();
		
		/* Secret Rooms */
		//SecretRoomsModule.register();
		
		/* Carpenter's Blocks */
		CarpentersModule.register();	

		/* Pam's HarvestCraft */
		HarvestcraftModule.register();
		
		/* Magical crops */
		MagicalCropsModule.register();		
		
		/* Statues */
		StatuesModule.register();
		
		/* Agriculture */
		AgricultureModule.register();		
		
		if(Loader.isModLoaded("ForgeMultipart")){
			HUDHandlerFMP.register();
			DecoratorFMP.register();
		}
		
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   Block.class);
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   TileEntity.class);
	}
	
	public void registerIMCs(){
		for (String s : ModuleRegistrar.instance().IMCRequests.keySet()){
			this.callbackRegistration(s, ModuleRegistrar.instance().IMCRequests.get(s));
		}
	}
	
	public void callbackRegistration(String method, String modname){
		String[] splitName = method.split("\\.");
		String methodName = splitName[splitName.length-1];
		String className  = method.substring(0, method.length()-methodName.length()-1);
		
		Waila.log.info(String.format("Trying to reflect %s %s", className, methodName));
		
		try{
			Class  reflectClass  = Class.forName(className);
			Method reflectMethod = reflectClass.getDeclaredMethod(methodName, IWailaRegistrar.class);
			reflectMethod.invoke(null, (IWailaRegistrar)ModuleRegistrar.instance());
			
			Waila.log.info(String.format("Success in registering %s", modname));
			
		} catch (ClassNotFoundException e){
			Waila.log.warn(String.format("Could not find class %s", className));
		} catch (NoSuchMethodException e){
			Waila.log.warn(String.format("Could not find method %s", methodName));
		} catch (Exception e){
			Waila.log.warn(String.format("Exception while trying to access the method : %s", e.toString()));
		}
	}	
	
	
	public Object getFont(){return null;}
}
