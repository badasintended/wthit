package mcp.mobius.waila.client;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderHealth;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderStack;
import mcp.mobius.waila.server.ProxyServer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import mcp.mobius.waila.cbcore.LangUtil;

public class ProxyClient extends ProxyServer {

	TrueTypeFont minecraftiaFont;
	
	//public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	public ProxyClient() {}
	
	
	@Override
	public void registerHandlers(){
		
	    LangUtil.loadLangDir("waila");
		
		minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/Minecraftia.ttf"), 14, true);
		
		//TickRegistry.registerTickHandler(WailaTickHandler.instance(), Side.CLIENT);		
		
		if (Loader.isModLoaded("NotEnoughItems")){
			try{
				Class.forName("mcp.mobius.waila.handlers.nei.NEIHandler").getDeclaredMethod("register").invoke(null);
			} catch (Exception e){
				Waila.log.error("Failed to hook into NEI properly. Reverting to Vanilla tooltip handler");
				MinecraftForge.EVENT_BUS.register(new VanillaTooltipHandler());    
			}
		} else {
			MinecraftForge.EVENT_BUS.register(new VanillaTooltipHandler());    
		}

		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBlocks(), Block.class);
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerBlocks(), Block.class);
		
		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerEntities(), Entity.class);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEntities(), Entity.class);		
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerEntities(), Entity.class);
		
		//ModuleRegistrar.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);

		ModuleRegistrar.instance().addConfig("General", "general.showents");		
		ModuleRegistrar.instance().addConfig("General", "general.showhp");
		ModuleRegistrar.instance().addConfig("General", "general.showcrop");
		
		ModuleRegistrar.instance().registerTooltipRenderer("waila.health",    new TTRenderHealth());
		ModuleRegistrar.instance().registerTooltipRenderer("waila.stack",     new TTRenderStack());
		ModuleRegistrar.instance().registerTooltipRenderer("waila.progress",  new TTRenderProgressBar());
	}	
	
	@Override
	public Object getFont(){return this.minecraftiaFont;}	

}
