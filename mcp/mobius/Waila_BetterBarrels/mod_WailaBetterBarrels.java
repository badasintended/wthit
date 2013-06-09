package mcp.mobius.Waila_BetterBarrels;

import java.util.logging.Logger;

import codechicken.nei.api.API;
import codechicken.nei.api.HUDAugmenterRegistry;
import codechicken.nei.api.HUDAugmenterRegistry.Layout;
import codechicken.nei.api.IHighlightHandler;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="Waila_BetterBarrels", name="Waila_BetterBarrels", version="0.0.1")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)

public class mod_WailaBetterBarrels {
    // The instance of your mod that Forge uses.
	@Instance("Waila_BetterBarrels")
	public static mod_WailaBetterBarrels instance;

	public static Logger log = Logger.getLogger("WailaBB");	

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
	}	
	
	@Init
	public void load(FMLInitializationEvent event) {
		API.registerHUDAugmenterTextHandler(new BetterBarrelsHUDHandler(), HUDAugmenterRegistry.Layout.BODY);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
	}
}