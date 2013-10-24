package mcp.mobius.waila;

import net.minecraft.item.Item;

import org.lwjgl.input.Keyboard;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
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
import mcp.mobius.waila.gui_old.NEI.HandlerTechTree;
import mcp.mobius.waila.gui_old.NEI.HandlerWiki;
import mcp.mobius.waila.handlers.SummaryProviderDefault;
import mcp.mobius.waila.handlers.hud.HUDHandlerExternal;
import mcp.mobius.waila.handlers.hud.HUDHandlerWaila;
import mcp.mobius.waila.handlers.nei.HandlerEnchants;
import mcp.mobius.waila.handlers.tooltip.TooltipHandlerWaila;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.server.ProxyServer;

public class ProxyClient extends ProxyServer {

	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		TickRegistry.registerTickHandler(new WailaTickHandler(), Side.CLIENT);		
		
		GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.HEADER);
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.BODY);
		API.registerHighlightHandler(new HUDHandlerWaila(),    ItemInfo.Layout.FOOTER);
		API.registerHighlightHandler(new HUDHandlerWaila(),    ItemInfo.Layout.HEADER);		
		
		KeyBindingRegistry.registerKeyBinding(new ConfigKeyHandler());
		
		// We mute the default keybind for displaying the tooltip
		NEIClientConfig.getSetting(Constants.BIND_NEI_SHOW).setIntValue(Keyboard.KEY_NONE);
		NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(false);
		
		GuiContainerManager.addInputHandler(new HandlerEnchants());
		API.addKeyBind(Constants.BIND_SCREEN_ENCH, Keyboard.KEY_RSHIFT);
		//API.addKeyBind(Constants.BIND_WIKI, "Display wiki",          Keyboard.KEY_RSHIFT);
		//API.addKeyBind(Constants.BIND_TECH, "Display techtree",      Keyboard.KEY_RSHIFT);
		
		ExternalModulesHandler.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);
		
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
