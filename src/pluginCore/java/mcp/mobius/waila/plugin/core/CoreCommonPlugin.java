package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.IWailaConfig.Overlay.Position.Align;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.provider.data.NameableBlockDataProvider;
import net.minecraft.world.Nameable;

public class CoreCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(WailaConstants.CONFIG_SHOW_BLOCK, true);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_FLUID, false);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_ENTITY, true);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_ICON, true);
        registrar.localConfig(WailaConstants.CONFIG_ICON_POSITION, Align.Y.MIDDLE);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_MOD_NAME, true);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_ITEM_MOD_NAME, true);
        registrar.localConfig(WailaConstants.CONFIG_SHOW_REGISTRY, false);

        registrar.blockData(NameableBlockDataProvider.INSTANCE, Nameable.class);
    }

}
