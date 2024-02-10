package mcp.mobius.waila.plugin.harvest;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.plugin.harvest.provider.HarvestProvider;
import net.minecraft.world.level.block.Block;

public class WailaPluginHarvest implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(HarvestProvider.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.addEventListener(HarvestProvider.INSTANCE);
    }

}
