package mcp.mobius.waila;

import org.lwjgl.input.Keyboard;

import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

import mcp.mobius.waila.addons.buildcraft.BCModule;
import mcp.mobius.waila.addons.enderstorage.EnderStorageModule;
import mcp.mobius.waila.addons.ic2.IC2Module;
import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
import mcp.mobius.waila.gui.ConfigKeyHandler;
import mcp.mobius.waila.handlers.EnchantmentHandler;
import mcp.mobius.waila.handlers.HUDHandlerExternal;
import mcp.mobius.waila.handlers.HUDHandlerWaila;
import mcp.mobius.waila.handlers.TooltipHandlerWaila;
import mcp.mobius.waila.handlers.WikiHandler;
import mcp.mobius.waila.server.ProxyServer;

public class ProxyClient extends ProxyServer {

	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.HEADER);
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.BODY);
		API.registerHighlightHandler(new HUDHandlerWaila(),    ItemInfo.Layout.FOOTER);
		API.registerHighlightHandler(new HUDHandlerWaila(),    ItemInfo.Layout.HEADER);		
		
		KeyBindingRegistry.registerKeyBinding(new ConfigKeyHandler());
		
		GuiContainerManager.addInputHandler(new WikiHandler());
		//API.addKeyBind("showenchant", "Display enchantements", Keyboard.KEY_RSHIFT);
		API.addKeyBind("showwiki", "Display wiki", Keyboard.KEY_RSHIFT);
		
		this.registerMods();		
	}	

	public void registerMods(){
		
		HUDHandlerVanilla.register();
		
		/* BETTER BARRELS */
		/*
		try {
			Class ModBetterBarrels = Class.forName("mcp.mobius.betterbarrels.mod_BetterBarrels");
			mod_Waila.log.log(Level.INFO, "BetterBarrel mod found.");
			HUDHandlerBetterBarrels.register();
			ConfigHandler.instance().addConfig("BetterBarrels", "betterbarrels.content", "Barrel content");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "BetterBarrel mod not found. Skipping.");			
		}
		*/
		
		/* BUILDCRAFT */
		BCModule.register();
		
		/* INDUSTRIALCRAFT2 */
		IC2Module.register();
		
		/*Thaumcraft*/
		ThaumcraftModule.register();
		
		/*EnderStorage*/
		EnderStorageModule.register();
		
	}	
	
}
