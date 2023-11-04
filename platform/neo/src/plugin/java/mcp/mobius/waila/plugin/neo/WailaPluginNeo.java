package mcp.mobius.waila.plugin.neo;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.neo.fluid.NeoFluidDescriptor;
import mcp.mobius.waila.plugin.neo.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.neo.provider.ItemCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class WailaPluginNeo implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        FluidData.describeFluid(Fluid.class, NeoFluidDescriptor.INSTANCE);
        registrar.addBlockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
