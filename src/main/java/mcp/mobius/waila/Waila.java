package mcp.mobius.waila;

import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.commands.CommandDumpHandlers;
import mcp.mobius.waila.config.ColorConfig;
import mcp.mobius.waila.network.NetworkHandler;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.DecoratorRenderer;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Set;

@Mod(modid = Waila.MODID, name = Waila.NAME, version = Waila.VERSION, dependencies = Waila.DEPEND, acceptedMinecraftVersions = "[1.11,1.12]", acceptableRemoteVersions = "*", guiFactory = "mcp.mobius.waila.gui.ConfigGuiFactory")
public class Waila {

    public static final String MODID = "waila";
    public static final String NAME = "Waila";
    public static final String VERSION = "@VERSION@";
    public static final String DEPEND = "";
    public static final Logger LOGGER = LogManager.getLogger("Waila");

    // The instance of your mod that Forge uses.
    @Instance(MODID)
    public static Waila instance;
    @SidedProxy(clientSide = "mcp.mobius.waila.client.ProxyClient", serverSide = "mcp.mobius.waila.server.ProxyServer")
    public static ProxyServer proxy;
    public static Set<ASMDataTable.ASMData> plugins;
    public static File configDir;
    public static File themeDir;

    public boolean serverPresent = false;

    /* INIT SEQUENCE */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        plugins = event.getAsmData().getAll(WailaPlugin.class.getCanonicalName());

        configDir = new File(event.getModConfigurationDirectory(), "waila");
        themeDir = new File(configDir, "theme");
        ConfigHandler.instance().loadDefaultConfig(event);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            ColorConfig.init();
        OverlayConfig.updateColors();
        WailaPacketHandler.INSTANCE.ordinal();
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());
            MinecraftForge.EVENT_BUS.register(new KeyEvent());
            MinecraftForge.EVENT_BUS.register(WailaTickHandler.instance());
        }
        MinecraftForge.EVENT_BUS.register(new NetworkHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerHandlers();
        ModIdentification.init();
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.registerMods();
        proxy.registerIMCs();
    }

    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (IMCMessage imcMessage : event.getMessages()) {
            if (!imcMessage.isStringMessage()) continue;

            if (imcMessage.key.equalsIgnoreCase("addconfig")) {
                String[] params = imcMessage.getStringValue().split("\\$\\$");
                if (params.length != 3) {
                    Waila.LOGGER.warn(String.format("Error while parsing config option from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));
                    continue;
                }
                Waila.LOGGER.info(String.format("Receiving config request from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ConfigHandler.instance().addConfig(params[0], params[1], params[2]);
            }

            if (imcMessage.key.equalsIgnoreCase("register")) {
                Waila.LOGGER.info(String.format("Receiving registration request from [ %s ] for method %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ModuleRegistrar.instance().addIMCRequest(imcMessage.getStringValue(), imcMessage.getSender());
            }
        }
    }


    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDumpHandlers());
    }
}