package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;

public interface IMixinService {

    IMixinService INSTANCE = Internals.loadService(IMixinService.class);

    void ReloadableServerResources_updateRegistryTags(RegistryAccess registryAccess);

    void ClientPacketListener_handleUpdateTags(LayeredRegistryAccess<ClientRegistryLayer> registryAccess);

    void onLanguageReloaded();

    void saveConfig();

}
