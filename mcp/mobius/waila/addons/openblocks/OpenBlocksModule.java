package mcp.mobius.waila.addons.openblocks;

import java.util.logging.Level;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class OpenBlocksModule {
	
	public static Class ModOpenBlocks  = null;
	public static Class TileEntityTank = null;	
	
	public static void register(){
		try{
			Class ModOpenBlocks = Class.forName("openblocks.OpenBlocks");
			mod_Waila.log.log(Level.INFO, "OpenBlocks mod found.");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "[OpenBlocks] OpenBlocks mod not found.");
			return;
		}			
		
		try{
			TileEntityTank = Class.forName("openblocks.common.tileentity.TileEntityTank");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[OpenBlocks] Class not found. " + e);
			return;
		}
		
		ModuleRegistrar.instance().addConfigRemote("OpenBlocks", "openblocks.fluidamount");		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerTank(),  TileEntityTank);
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerTank(),  TileEntityTank);		
	}
}
