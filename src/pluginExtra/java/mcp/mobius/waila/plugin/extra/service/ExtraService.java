package mcp.mobius.waila.plugin.extra.service;

import com.google.common.base.Preconditions;
import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.extra.WailaPluginExtra;
import mcp.mobius.waila.plugin.extra.data.EnergyDescription;
import mcp.mobius.waila.plugin.extra.data.FluidDescription;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ExtraService implements IExtraService {

    @Override
    public <T extends BuiltinData> void bootstrapData(Class<T> type) {
        WailaPluginExtra.BOOTSTRAPPED.add(type);
    }

    @Override
    public <T extends BuiltinData> void assertDataBootstrapped(Class<T> type) {
        Preconditions.checkState(WailaPluginExtra.BOOTSTRAPPED.contains(type));
    }

    @Override
    public EnergyData.Description setEnergyDescFor(String namespace) {
        EnergyDescription defaults = new EnergyDescription();
        EnergyDescription.MAP.put(namespace, defaults);
        return defaults;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Fluid> void setFluidDescFor(T fluid, FluidData.FluidDescriptor<T> descriptor) {
        FluidDescription.FLUID_STATIC.put(fluid, (FluidData.FluidDescriptor<Fluid>) descriptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Fluid> void setFluidDescFor(Class<T> clazz, FluidData.FluidDescriptor<T> descriptor) {
        FluidDescription.FLUID_DYNAMIC.put(clazz, (FluidData.FluidDescriptor<Fluid>) descriptor);
    }

    @Override
    public void setCauldronDescFor(Block block, FluidData.CauldronDescriptor getter) {
        FluidDescription.CAULDRON_STATIC.put(block, getter);
    }

    @Override
    public void setCauldronDescFor(Class<? extends Block> clazz, FluidData.CauldronDescriptor getter) {
        FluidDescription.CAULDRON_DYNAMIC.put(clazz, getter);
    }

}
