package mcp.mobius.waila.plugin.neo;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.neo.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.ItemCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NeoCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.addBlockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
