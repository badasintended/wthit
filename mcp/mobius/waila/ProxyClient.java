package mcp.mobius.waila;

import java.util.logging.Level;

import codechicken.nei.api.API;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.buildcraft.HUDHandlerBCTanks;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2Generator;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2Machine;
import mcp.mobius.waila.addons.ic2.HUDHandlerIC2Storage;
import mcp.mobius.waila.gui.ConfigKeyHandler;
import mcp.mobius.waila.handlers.HUDHandlerExternal;
import mcp.mobius.waila.handlers.HUDHandlerWaila;
import mcp.mobius.waila.handlers.TooltipHandlerWaila;

public class ProxyClient extends ProxyServer {

	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		//GuiContainerManager.addInputHandler(new EnchantementHandler());
		API.registerHighlightHandler(new HUDHandlerWaila(), ItemInfo.Layout.FOOTER);
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.HEADER);
		API.registerHighlightHandler(new HUDHandlerExternal(), ItemInfo.Layout.BODY);
		
		//API.registerHighlightIdentifier(2000, new HUDHandlerExternal());
		KeyBindingRegistry.registerKeyBinding(new ConfigKeyHandler());
		//API.addKeyBind("showenchant", "Display enchantements", Keyboard.KEY_RSHIFT);
		this.registerMods();		
	}	

	public void registerMods(){
		
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
		try {
			Class ModBuildcraftFactory = Class.forName("buildcraft.BuildCraftFactory");
			mod_Waila.log.log(Level.INFO, "Buildcraft|Factory mod found.");
			HUDHandlerBCTanks.register();
			ConfigHandler.instance().addConfig("Buildcraft", "bc.tankamount", "Liquid amount");
			ConfigHandler.instance().addConfig("Buildcraft", "bc.tanktype",   "Liquid type");
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "Buildcraft|Factory mod not found. Skipping.");			
		}		
		
		/* INDUSTRIALCRAFT2 */
		try {
			Class ModIndustrialCraft = Class.forName("ic2.core.IC2");
			mod_Waila.log.log(Level.INFO, "Industrialcraft2 mod found.");
			HUDHandlerIC2Storage.register();
			HUDHandlerIC2Machine.register();
			HUDHandlerIC2Generator.register();
			ConfigHandler.instance().addConfig("IndustrialCraft2", "ic2.inputeu",  "Max EU input");
			ConfigHandler.instance().addConfig("IndustrialCraft2", "ic2.outputeu", "Max EU output");			
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.INFO, "Industrialcraft2 mod not found. Skipping.");			
		}		
		
	}	
	
}
