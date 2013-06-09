package mcp.mobius.Waila_Buildcraft;

import java.util.logging.Logger;

import codechicken.nei.api.API;
import codechicken.nei.api.HUDAugmenterRegistry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="Waila_BC", name="Waila_Buildcraft", version="0.0.1")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)

public class mod_WailaBuildcraft {
    // The instance of your mod that Forge uses.
	@Instance("Waila_BC")
	public static mod_WailaBuildcraft instance;

	public static Logger log = Logger.getLogger("WailaBC");	

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
	}	
	
	@Init
	public void load(FMLInitializationEvent event) {
		API.registerHUDAugmenterTextHandler(new BuildcraftHUDHandler(), HUDAugmenterRegistry.Layout.HEADER);		
		API.registerHUDAugmenterTextHandler(new BuildcraftHUDHandler(), HUDAugmenterRegistry.Layout.BODY);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
}