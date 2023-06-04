package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IExtraService {

    IExtraService INSTANCE = Internals.loadService(IExtraService.class);

    <T extends BuiltinData> void bootstrapData(Class<T> type);

    <T extends BuiltinData> void assertDataBootstrapped(Class<T> type);

    EnergyData.Description setEnergyDescFor(String namespace);

    <T extends Fluid> void setFluidDescFor(T fluid, FluidData.Descriptor<T> descriptor);

    <T extends Fluid> void setFluidDescFor(Class<T> clazz, FluidData.Descriptor<T> descriptor);

}
