package mcp.mobius.waila.addons.projectred;

import java.io.File;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ProjectRedModule {

	public static void register(){	
		try{
			Class ModClass = Class.forName("mrtjp.projectred.ProjectRedIntegration");
			Waila.log.log(Level.INFO, "ProjectRed|Integration mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[ProjectRed] ProjectRed|Integration mod not found.");
			return;
		}
		
		ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showio");	
		ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showdata");	
		
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_sgate");
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_igate");
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_tgate");
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_bgate");
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_agate");
		ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_rgate");
		
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_sgate");
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_igate");
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_tgate");
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_bgate");
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_agate");
		ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_rgate");		
		
		//ModuleRegistrar.instance().registerBlockDecorator(new HUDDecoratorRsGateLogic(), BlockMultipart);
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerRsGateLogic(), BlockMultipart);
		//ModuleRegistrar.instance().registerSyncedNBTKey("*", BlockMultipart);
	}	
	
}
