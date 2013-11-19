package mcp.mobius.waila.addons.projectred;

import java.io.File;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;

public class ProjectRedModule {


	public static Class BlockMultipart = null;
	
	public static void register(){	
		try{
			Class ModClass = Class.forName("mrtjp.projectred.ProjectRedIntegration");
			mod_Waila.log.log(Level.INFO, "ProjectRed|Integration mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[ProjectRed] ProjectRed|Integration mod not found.");
			return;
		}
		
		try{
			
			//InstancedRsGateLogic = Class.forName("mrtjp.projectred.integration.InstancedRsGateLogic");
			BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Class not found. " + e);
			return;
//		} catch (NoSuchMethodException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Method not found." + e);
//			return;			
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Field not found." + e);
//			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[EnderStorage] Unhandled exception." + e);
			return;			
		}
		
		ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorRsGateLogic(), BlockMultipart);
	}	
	
}
