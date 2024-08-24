package mcp.mobius.waila.plugin.harvest;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.plugin.harvest.provider.HarvestProvider;
import net.minecraft.world.level.block.Block;

public class HarvestClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(HarvestProvider.INSTANCE, Block.class);
        registrar.eventListener(HarvestProvider.INSTANCE, 3000);
    }

}
