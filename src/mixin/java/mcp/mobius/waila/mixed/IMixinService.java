package mcp.mobius.waila.mixed;

import mcp.mobius.waila.api.__internal__.Internals;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;

public interface IMixinService {

    IMixinService INSTANCE = Internals.loadService(IMixinService.class);

    void attachRegistryFilter(RegistryAccess registryAccess);

    void onServerLogin();

    void onGuiRender(GuiGraphics ctx, DeltaTracker delta);

    void onLanguageReloaded();

    void saveConfig();

}
