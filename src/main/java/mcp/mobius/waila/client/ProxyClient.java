package mcp.mobius.waila.client;

import mcp.mobius.waila.addons.agriculture.AgricultureModule;
import mcp.mobius.waila.addons.buildcraft.BCModule;
import mcp.mobius.waila.addons.buildcraft.BCPowerAPIModule;
import mcp.mobius.waila.addons.carpenters.CarpentersModule;
import mcp.mobius.waila.addons.enderio.EnderIOModule;
import mcp.mobius.waila.addons.enderstorage.EnderStorageModule;
import mcp.mobius.waila.addons.etb.ETBModule;
import mcp.mobius.waila.addons.exu.ExtraUtilitiesModule;
import mcp.mobius.waila.addons.gravestone.GravestoneModule;
import mcp.mobius.waila.addons.harvestcraft.HarvestcraftModule;
import mcp.mobius.waila.addons.ic2.IC2Module;
import mcp.mobius.waila.addons.magicalcrops.MagicalCropsModule;
import mcp.mobius.waila.addons.openblocks.OpenBlocksModule;
import mcp.mobius.waila.addons.projectred.ProjectRedModule;
import mcp.mobius.waila.addons.railcraft.RailcraftModule;
import mcp.mobius.waila.addons.secretrooms.SecretRoomsModule;
import mcp.mobius.waila.addons.statues.StatuesModule;
import mcp.mobius.waila.addons.stevescarts.StevesCartsModule;
import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;
import mcp.mobius.waila.addons.thermalexpansion.ThermalExpansionModule;
import mcp.mobius.waila.addons.twilightforest.TwilightForestModule;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.DecoratorFMP;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.handlers.HUDHandlerFMP;
import mcp.mobius.waila.handlers.nei.ModNameFilter;
//import mcp.mobius.waila.handlers.SummaryProviderDefault;
import mcp.mobius.waila.handlers.nei.TooltipHandlerWaila;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import mcp.mobius.waila.cbcore.LangUtil;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class ProxyClient extends ProxyServer {

	TrueTypeFont minecraftiaFont;
	
	//public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		
	    LangUtil.loadLangDir("waila");
		
		minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/Minecraftia.ttf"), 14, true);
		
		//TickRegistry.registerTickHandler(WailaTickHandler.instance(), Side.CLIENT);		
		
		//GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NEWFILTERS, true))
			API.addSearchProvider(new ModNameFilter());
		
		GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());
		
		//KeyBindingRegistry.registerKeyBinding(new ConfigKeyHandler());
		
		// We mute the default keybind for displaying the tooltip
		NEIClientConfig.getSetting(Constants.BIND_NEI_SHOW).setIntValue(Keyboard.KEY_NONE);
		NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(false);
		
		//API.addKeyBind(Constants.BIND_WIKI, "Display wiki",          Keyboard.KEY_RSHIFT);
		//API.addKeyBind(Constants.BIND_TECH, "Display techtree",      Keyboard.KEY_RSHIFT);

		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBlocks(), Block.class);
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerBlocks(), Block.class);
		
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerEntities(), Entity.class);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEntities(), Entity.class);		
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerEntities(), Entity.class);
		
		//ModuleRegistrar.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);
		
		ModuleRegistrar.instance().addConfig("General", "general.showhp");
		ModuleRegistrar.instance().addConfig("General", "general.showcrop");		
	}	

	@Override
	public void registerMods(){
		
		HUDHandlerVanilla.register();
		
		/* BUILDCRAFT */
		BCModule.register();
		
		/* INDUSTRIALCRAFT2 */
		//IC2Module.register();
		
		/*Thaumcraft*/
		ThaumcraftModule.register();
		
		/*EnderStorage*/
		EnderStorageModule.register();
		
		/*Gravestone*/
		GravestoneModule.register();
		
		/*Twilight forest*/
		TwilightForestModule.register();
		
		/* Thermal Expansion */
		ThermalExpansionModule.register();
		
		/* ETB */
		ETBModule.register();
		
		/* EnderIO */
		EnderIOModule.register();		
		
		/* Buildcraft Power API */
		//BCPowerAPIModule.register();
		
		/* ProjectRed API */
		ProjectRedModule.register();
		
		/* ExtraUtilities */
		ExtraUtilitiesModule.register();	
		
		/* OpenBlocks */
		OpenBlocksModule.register();
		
		/* Railcraft */
		RailcraftModule.register();		
		
		/* Steve's Carts */
		StevesCartsModule.register();
		
		/* Secret Rooms */
		//SecretRoomsModule.register();
		
		/* Carpenter's Blocks */
		CarpentersModule.register();	

		/* Pam's HarvestCraft */
		HarvestcraftModule.register();
		
		/* Magical crops */
		MagicalCropsModule.register();		
		
		/* Statues */
		StatuesModule.register();
		
		/* Agriculture */
		AgricultureModule.register();		
		
		if(Loader.isModLoaded("ForgeMultipart")){
			HUDHandlerFMP.register();
			DecoratorFMP.register();
		}
		
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   Block.class);
		//ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   TileEntity.class);
	}	
	
	@Override
	public Object getFont(){return this.minecraftiaFont;}	
}
