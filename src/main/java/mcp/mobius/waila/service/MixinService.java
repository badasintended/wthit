package mcp.mobius.waila.service;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.mixed.IMixinService;
import mcp.mobius.waila.registry.RegistryFilter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;

public class MixinService implements IMixinService {

    @Override
    public void attachRegistryFilter(RegistryAccess registryAccess) {
        RegistryFilter.attach(registryAccess);
    }

    @Override
    public void onServerLogin() {
        WailaClient.onServerLogIn();
    }

    @Override
    public void onGuiRender(GuiGraphics ctx, DeltaTracker delta) {
        TooltipRenderer.render(ctx, delta);
    }

    @Override
    public void onLanguageReloaded() {
        JsonConfig.reloadAllInstances();
        PluginConfig.write();
    }

}
