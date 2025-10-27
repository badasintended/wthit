package mcp.mobius.waila.service;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.mixed.IMixinService;
import mcp.mobius.waila.registry.RegistryFilter;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;

public class MixinService implements IMixinService {

    @Override
    public void ReloadableServerResources_updateRegistryTags(RegistryAccess registryAccess) {
        RegistryFilter.attach(registryAccess);
    }

    @Override
    public void ClientPacketListener_handleUpdateTags(LayeredRegistryAccess<ClientRegistryLayer> registryAccess) {
        RegistryFilter.attach(registryAccess.compositeAccess());
    }

    @Override
    public void onLanguageReloaded() {
        JsonConfig.reloadAllInstances();
        PluginConfig.write();
    }

    @Override
    public void saveConfig() {
        Waila.CONFIG.save();
    }

}
