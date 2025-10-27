package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.core.RegistryAccess;

public interface IMixinService {

    IMixinService INSTANCE = Internals.loadService(IMixinService.class);

    void ReloadableServerResources_updateRegistryTags(RegistryAccess registryAccess);

    void ClientPacketListener_handleUpdateTags(RegistryAccess.Frozen registryAccess);

    void onLanguageReloaded();

    void saveConfig();

}
