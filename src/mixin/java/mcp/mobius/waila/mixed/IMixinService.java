package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ToolMaterial;

public interface IMixinService {

    IMixinService INSTANCE = Internals.loadService(IMixinService.class);

    void attachRegistryFilter(RegistryAccess registryAccess);

    void onServerLogin();

    void addToolMaterialInstance(ToolMaterial material);

    void onLanguageReloaded();

}
