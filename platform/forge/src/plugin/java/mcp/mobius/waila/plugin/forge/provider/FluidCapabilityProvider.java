package mcp.mobius.waila.plugin.forge.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public enum FluidCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.class, res ->
            accessor.getTarget().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(handler -> {
                int size = handler.getTanks();
                FluidData fluidData = FluidData.of(FluidData.Unit.MILLIBUCKETS, size);

                for (int i = 0; i < size; i++) {
                    FluidStack stack = handler.getFluidInTank(i);
                    fluidData.add(stack.getFluid(), stack.getTag(), stack.getAmount(), handler.getTankCapacity(i));
                }

                res.add(fluidData);
            }));
    }

}
