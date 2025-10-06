package mcp.mobius.waila.plugin.extra.service;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.__internal__.IExtraService;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.plugin.extra.data.EnergyDataImpl;
import mcp.mobius.waila.plugin.extra.data.FluidDataImpl;
import mcp.mobius.waila.plugin.extra.data.ItemDataImpl;
import mcp.mobius.waila.plugin.extra.data.ProgressDataImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class ExtraService implements IExtraService {

    @Override
    public EnergyData.Description setEnergyDescFor(String namespace) {
        var defaults = new EnergyDataImpl.Description();
        EnergyDataImpl.Description.MAP.put(namespace, defaults);
        return defaults;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Fluid> void setFluidDescFor(T fluid, FluidData.FluidDescriptor<T> descriptor) {
        FluidDataImpl.FluidDescription.FLUID_STATIC.put(fluid, (FluidData.FluidDescriptor<Fluid>) descriptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Fluid> void setFluidDescFor(Class<T> clazz, FluidData.FluidDescriptor<T> descriptor) {
        FluidDataImpl.FluidDescription.FLUID_DYNAMIC.put(clazz, (FluidData.FluidDescriptor<Fluid>) descriptor);
    }

    @Override
    public void setCauldronDescFor(Block block, FluidData.CauldronDescriptor getter) {
        FluidDataImpl.FluidDescription.CAULDRON_STATIC.put(block, getter);
    }

    @Override
    public void setCauldronDescFor(Class<? extends Block> clazz, FluidData.CauldronDescriptor getter) {
        FluidDataImpl.FluidDescription.CAULDRON_DYNAMIC.put(clazz, getter);
    }

    @Override
    public EnergyData createEnergyData(double stored, double capacity) {
        return new EnergyDataImpl(stored, capacity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> FluidData.PlatformDependant<S> createFluidData(@Nullable FluidData.PlatformTranslator<S> proxy, FluidData.Unit unit, int slotCountHint) {
        return (FluidData.PlatformDependant<S>) new FluidDataImpl((FluidData.PlatformTranslator<Object>) proxy, unit, slotCountHint);
    }

    @Override
    public ItemData createItemData(IPluginConfig config) {
        return new ItemDataImpl(config);
    }

    @Override
    public ProgressData createRatioProgressData(float ratio) {
        return new ProgressDataImpl(ratio);
    }

    @Override
    public ProgressData createTickProgressData(int current, int total) {
        return new ProgressDataImpl(current, total);
    }

}
