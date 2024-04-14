package mcp.mobius.waila.plugin.harvest;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.plugin.harvest.config.HarvestDisplayMode;
import mcp.mobius.waila.plugin.harvest.config.Options;
import mcp.mobius.waila.plugin.harvest.provider.HarvestProvider;
import net.minecraft.world.level.block.Block;

public class WailaPluginHarvest implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addFeatureConfig(Options.ENABLED, true);
        registrar.addConfig(Options.DISPLAY_MODE, HarvestDisplayMode.MODERN);
        registrar.addComponent(HarvestProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.addEventListener(HarvestProvider.INSTANCE, 3000);

        if (IApiService.INSTANCE.isDevEnv()) {
            registrar.addConfig(Options.DEV_DISABLE_CACHE, false);
        }
    }

}
