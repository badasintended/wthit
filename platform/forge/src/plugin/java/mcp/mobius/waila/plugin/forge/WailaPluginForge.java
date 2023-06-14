package mcp.mobius.waila.plugin.forge;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.plugin.forge.fluid.ForgeFluidDescriptor;
import mcp.mobius.waila.plugin.forge.provider.EnergyCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.FluidCapabilityProvider;
import mcp.mobius.waila.plugin.forge.provider.ItemCapabilityProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public class WailaPluginForge implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        BuiltinData.bootstrap(EnergyData.class, FluidData.class, ItemData.class);
        FluidData.describe(Fluid.class, ForgeFluidDescriptor.INSTANCE);
        registrar.addBlockData(EnergyCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(ItemCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidCapabilityProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
