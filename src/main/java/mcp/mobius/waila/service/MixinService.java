package mcp.mobius.waila.service;

import java.util.LinkedHashSet;
import java.util.Set;

import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.mixed.IMixinService;
import mcp.mobius.waila.registry.RegistryFilter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ToolMaterial;

public class MixinService implements IMixinService {

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
    public void addToolMaterialInstance(ToolMaterial material) {
        TOOL_MATERIALS.add(material);
    }

    @Override
    public void onLanguageReloaded() {
        JsonConfig.reloadAllInstances();
        PluginConfig.write();
    }

}
