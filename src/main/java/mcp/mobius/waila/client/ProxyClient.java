package mcp.mobius.waila.client;

import java.lang.reflect.Method;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
//import mcp.mobius.waila.handlers.SummaryProviderDefault;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
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
			} catch (Exception e){}
		}

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
	public Object getFont(){return this.minecraftiaFont;}	

}
