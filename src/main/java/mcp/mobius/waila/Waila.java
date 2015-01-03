package mcp.mobius.waila;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.network.NetworkHandler;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.OverlayConfig;
import mcp.mobius.waila.overlay.DecoratorRenderer;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.commands.CommandDumpHandlers;

@Mod(modid="Waila", name="Waila", version="1.5.8", dependencies="required-after:NotEnoughItems@[1.0.4.0,)", acceptableRemoteVersions="*")
/*
@NetworkMod(channels = {"Waila"},clientSideRequired=false, serverSideRequired=false, connectionHandler = WailaConnectionHandler.class, 
			packetHandler = WailaPacketHandler.class, versionBounds="[1.5.0,)")
			//packetHandler = WailaPacketHandler.class)
*/

public class Waila {
    // The instance of your mod that Forge uses.
	@Instance("Waila")
	public static Waila instance;

	@SidedProxy(clientSide="mcp.mobius.waila.client.ProxyClient", serverSide="mcp.mobius.waila.server.ProxyServer")
	public static ProxyServer proxy;	
	public static Logger log = LogManager.getLogger("Waila");
	public boolean serverPresent = false;
	
    /* INIT SEQUENCE */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	ConfigHandler.instance().loadDefaultConfig(event);
		OverlayConfig.updateColors();
		MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());	
		WailaPacketHandler.INSTANCE.ordinal();
	}	
	
    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        try {
            Field eBus = FMLModContainer.class.getDeclaredField("eventBus");
            eBus.setAccessible(true);
            EventBus FMLbus = (EventBus) eBus.get(FMLCommonHandler.instance().findContainerFor(this));
            FMLbus.register(this);
        } catch (Throwable t) {}
        
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());
    		FMLCommonHandler.instance().bus().register(new KeyEvent());
    		FMLCommonHandler.instance().bus().register(WailaTickHandler.instance());        	
        }
		FMLCommonHandler.instance().bus().register(new NetworkHandler());        
	}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		proxy.registerHandlers();
    	ModIdentification.init();
        
    	/*
        if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_KEYBIND, true)){
        
	        for (String key: ModIdentification.keyhandlerStrings.keySet()){
	        	String orig  = I18n.getString(key);
	        	if (orig.equals(key))
	        		orig = LanguageRegistry.instance().getStringLocalization(key);
	        	if (orig.equals(key))
	        		orig = LangUtil.translateG(key);	        	
	        	if (orig.isEmpty())
	        		orig = key;
	        	
	        	String modif;
	        	if (orig.startsWith("[") || orig.contains(":"))
	        		modif = orig;
	        	else{
	        		String id = ModIdentification.keyhandlerStrings.get(key);
	        		
	        		if (id.contains("."))
	        			id = id.split("\\.")[0];
	        		
	        		if (id.length() > 10)
	        			id = id.substring(0, 11);
	        		
	        		if (id.isEmpty())
	        			id = "????";
	        		
	        		modif = String.format("[%s] %s", id, orig);
	        	}
	
	        	LanguageRegistry.instance().addStringLocalization(key, modif);
	        }
        }
        */
        
	}

    @Subscribe
    public void loadComplete(FMLLoadCompleteEvent event) {    
    	proxy.registerMods();
    	proxy.registerIMCs();
    	
    	/*
    	String[] ores = OreDictionary.getOreNames();
    	for (String s : ores)
    		for (ItemStack stack : OreDictionary.getOres(s))
    			System.out.printf("%s : %s\n", s, stack);
    	*/
    }    
    	
    @EventHandler
	public void processIMC(FMLInterModComms.IMCEvent event)
	{
		for (IMCMessage imcMessage : event.getMessages()){
			if (!imcMessage.isStringMessage()) continue;
			
			if (imcMessage.key.equalsIgnoreCase("addconfig")){
				String[] params = imcMessage.getStringValue().split("\\$\\$");
				if (params.length != 3){
					Waila.log.warn(String.format("Error while parsing config option from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));
					continue;
				}
				Waila.log.info(String.format("Receiving config request from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));				
				ConfigHandler.instance().addConfig(params[0], params[1], params[2]);
			}
			
			if (imcMessage.key.equalsIgnoreCase("register")){
				Waila.log.info(String.format("Receiving registration request from [ %s ] for method %s", imcMessage.getSender(), imcMessage.getStringValue()));
				ModuleRegistrar.instance().addIMCRequest(imcMessage.getStringValue(), imcMessage.getSender());
			}
		}
	}		
	

	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandDumpHandlers());
	}	
}