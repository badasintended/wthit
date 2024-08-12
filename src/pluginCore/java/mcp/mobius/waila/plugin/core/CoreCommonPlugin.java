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
        registrar.addConfig(WailaConstants.CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_FLUID, false);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ICON, true);
        registrar.addConfig(WailaConstants.CONFIG_ICON_POSITION, Align.Y.MIDDLE);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ITEM_MOD_NAME, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_REGISTRY, false);

        registrar.addBlockData(NameableBlockDataProvider.INSTANCE, Nameable.class);
    }

}
