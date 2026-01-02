package mcp.mobius.waila.plugin.forge;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.forge.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.ItemCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ForgeCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.blockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.blockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.blockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
