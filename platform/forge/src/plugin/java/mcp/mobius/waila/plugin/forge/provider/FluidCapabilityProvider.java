package mcp.mobius.waila.plugin.forge.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.forge.ForgeFluidData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public enum FluidCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.TYPE, res ->
            accessor.getTarget().getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
                var size = handler.getTanks();
                var fluidData = ForgeFluidData.of(size);

                for (var i = 0; i < size; i++) {
                    fluidData.add(handler.getFluidInTank(i), handler.getTankCapacity(i));
                }

                res.add(fluidData);
            }));
    }

}
