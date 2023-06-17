package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IExtraService {

    IExtraService INSTANCE = Internals.loadService(IExtraService.class);

    EnergyData.Description setEnergyDescFor(String namespace);

    <T extends Fluid> void setFluidDescFor(T fluid, FluidData.FluidDescriptor<T> descriptor);

    <T extends Fluid> void setFluidDescFor(Class<T> clazz, FluidData.FluidDescriptor<T> descriptor);

    void setCauldronDescFor(Block block, FluidData.CauldronDescriptor getter);

    void setCauldronDescFor(Class<? extends Block> clazz, FluidData.CauldronDescriptor getter);

}
