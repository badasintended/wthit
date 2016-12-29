package mcp.mobius.waila.proxy;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.config.ColorConfig;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.overlay.DecoratorRenderer;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderHealth;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ProxyClient extends ProxyCommon {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ColorConfig.init();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());
        MinecraftForge.EVENT_BUS.register(new KeyEvent());
        MinecraftForge.EVENT_BUS.register(WailaTickHandler.instance());

        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        MinecraftForge.EVENT_BUS.register(new VanillaTooltipHandler());

        //ModuleRegistrar.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);

        ModuleRegistrar.instance().registerTooltipRenderer("waila.health", new TTRenderHealth());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.stack", new TTRenderStack());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.progress", new TTRenderProgressBar());
    }
}
