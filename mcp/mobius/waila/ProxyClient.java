package mcp.mobius.waila;

import net.minecraft.item.Item;

import org.lwjgl.input.Keyboard;

import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.addons.appeng.AppEngModule;
import mcp.mobius.waila.addons.betterbarrels.BetterBarrelsModule;
import mcp.mobius.waila.addons.buildcraft.BCModule;
import mcp.mobius.waila.addons.enderstorage.EnderStorageModule;
import mcp.mobius.waila.addons.gravestone.GravestoneModule;
import mcp.mobius.waila.addons.ic2.IC2Module;
import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;
import mcp.mobius.waila.addons.twilightforest.TwilightForestModule;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
import mcp.mobius.waila.gui.ConfigKeyHandler;
import mcp.mobius.waila.handlers.HUDHandlerExternal;
import mcp.mobius.waila.handlers.HUDHandlerWaila;
import mcp.mobius.waila.handlers.HandlerEnchantment;
import mcp.mobius.waila.handlers.SummaryProviderDefault;
import mcp.mobius.waila.handlers.HandlerTechTree;
import mcp.mobius.waila.handlers.TooltipHandlerWaila;
import mcp.mobius.waila.handlers.HandlerWiki;
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

		ExternalModulesHandler.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);		
		
		//GuiContainerManager.addInputHandler(new HandlerEnchantment());		
		//GuiContainerManager.addInputHandler(new HandlerWiki());		
		//GuiContainerManager.addInputHandler(new HandlerTechTree());
		//API.addKeyBind(Constants.BIND_SCREEN_ENCH, "Display enchantements", Keyboard.KEY_NUMPAD7);
		//API.addKeyBind(Constants.BIND_SCREEN_WIKI, "Display wiki",          Keyboard.KEY_NUMPAD8);
		//API.addKeyBind(Constants.BIND_SCREEN_TECH, "Display techtree",      Keyboard.KEY_NUMPAD9);
		
		this.registerMods();		
	}	

	public void registerMods(){
		
		HUDHandlerVanilla.register();
		
		/*BETTER BARRELS*/
		//BetterBarrelsModule.register();
		
		/* BUILDCRAFT */
		BCModule.register();
		
		/* INDUSTRIALCRAFT2 */
		IC2Module.register();
		
		/*Thaumcraft*/
		ThaumcraftModule.register();
		
		/*EnderStorage*/
		EnderStorageModule.register();
		
		/*Gravestone*/
		GravestoneModule.register();
		
		/*Twilight forest*/
		TwilightForestModule.register();
		
		/* Applied Energetics */
		AppEngModule.register();
	}	
	
}
