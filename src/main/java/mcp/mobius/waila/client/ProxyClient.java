package mcp.mobius.waila.client;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderHealth;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderStack;
import mcp.mobius.waila.server.ProxyServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer {

    TrueTypeFont minecraftiaFont;

    @Override
    public void registerHandlers() {

//        minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/minecraftia.ttf"), 14, true);

        MinecraftForge.EVENT_BUS.register(new VanillaTooltipHandler());

        //ModuleRegistrar.instance().registerShortDataProvider(new SummaryProviderDefault(), Item.class);

        ModuleRegistrar.instance().registerTooltipRenderer("waila.health", new TTRenderHealth());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.stack", new TTRenderStack());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.progress", new TTRenderProgressBar());
    }

    @Override
    public Object getFont() {
        return this.minecraftiaFont;
    }

}
