package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;

public interface IMixedService {

    IMixedService INSTANCE = Internals.loadService(IMixedService.class);

    void ReloadableServerResources_updateRegistryTags(RegistryAccess registryAccess);

    void ClientPacketListener_handleUpdateTags(LayeredRegistryAccess<ClientRegistryLayer> registryAccess);

}
