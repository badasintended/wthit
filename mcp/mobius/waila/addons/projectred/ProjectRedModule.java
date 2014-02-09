package mcp.mobius.waila.addons.projectred;

import java.io.File;
import java.util.logging.Level;

import net.minecraft.block.Block;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ProjectRedModule {


	public static Class BlockMultipart = null;
	
	public static void register(){	
		try{
			Class ModClass = Class.forName("mrtjp.projectred.ProjectRedIntegration");
			Waila.log.log(Level.INFO, "ProjectRed|Integration mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[ProjectRed] ProjectRed|Integration mod not found.");
			return;
		}
		
		try{
			
			//InstancedRsGateLogic = Class.forName("mrtjp.projectred.integration.InstancedRsGateLogic");
			BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
//		} catch (NoSuchMethodException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
//			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
//			return;			
		} catch (Exception e){
			Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showio");	
		ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showdata");	
		
		ModuleRegistrar.instance().registerBlockDecorator(new HUDDecoratorRsGateLogic(), BlockMultipart);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerRsGateLogic(), BlockMultipart);
	}	
	
}
