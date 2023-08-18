package mcp.mobius.waila.service;

import mcp.mobius.waila.mixed.IMixedService;
import mcp.mobius.waila.registry.RegistryFilter;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;

public class MixedService implements IMixedService {

    @Override
    public void ReloadableServerResources_updateRegistryTags(RegistryAccess registryAccess) {
        RegistryFilter.attach(registryAccess);
    }

    @Override
    public void ClientPacketListener_handleUpdateTags(LayeredRegistryAccess<ClientRegistryLayer> registryAccess) {
        RegistryFilter.attach(registryAccess.compositeAccess());
    }

}
