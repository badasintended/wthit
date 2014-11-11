package mcp.mobius.waila.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandDumpHandlers extends CommandBase {

	@Override
	public String getCommandName() {
		return "dumphandlers";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/dumphandlers";
	}

	@Override
	public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
		
		System.out.printf("\n\n== HEAD BLOCK PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().headBlockProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaDataProvider provider : ModuleRegistrar.instance().headBlockProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}

		System.out.printf("\n\n== BODY BLOCK PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().bodyBlockProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaDataProvider provider : ModuleRegistrar.instance().bodyBlockProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}
		
		System.out.printf("\n\n== TAIL BLOCK PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().tailBlockProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaDataProvider provider : ModuleRegistrar.instance().tailBlockProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");			
		}		
		
		System.out.printf("\n\n== STACK BLOCK PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().stackBlockProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaDataProvider provider : ModuleRegistrar.instance().stackBlockProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}		

		System.out.printf("\n\n== HEAD ENTITY PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().headEntityProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaEntityProvider provider : ModuleRegistrar.instance().headEntityProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}

		System.out.printf("\n\n== BODY ENTITY PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().bodyEntityProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaEntityProvider provider : ModuleRegistrar.instance().bodyEntityProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}
		
		System.out.printf("\n\n== TAIL ENTITY PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().tailEntityProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaEntityProvider provider : ModuleRegistrar.instance().tailEntityProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}		
		
		System.out.printf("\n\n== STACK ENTITY PROVIDERS ==\n");
		for (Class clazz : ModuleRegistrar.instance().overrideEntityProviders.keySet()){
			System.out.printf("+ %s +\n", clazz.getName());
			for (IWailaEntityProvider provider : ModuleRegistrar.instance().overrideEntityProviders.get(clazz)){
				System.out.printf("  - %s\n", provider.getClass().getName());
			}
			System.out.printf("\n");
		}		

	}

	@Override
    public int getRequiredPermissionLevel(){ return 3; }	

	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender){
		//if ((sender instanceof EntityPlayerMP) && ((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;
		return super.canCommandSenderUseCommand(sender);
    }	
	
}
