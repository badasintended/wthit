package mcp.mobius.waila.service;

import java.util.LinkedHashSet;
import java.util.Set;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.mixed.IMixedService;
import mcp.mobius.waila.registry.RegistryFilter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ToolMaterial;

public class MixedService implements IMixedService {

    public static final Set<ToolMaterial> TOOL_MATERIALS = new LinkedHashSet<>();

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
    public void addToolMaterialInstance(ToolMaterial material) {
        TOOL_MATERIALS.add(material);
    }

}
