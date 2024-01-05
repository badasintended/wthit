package mcp.mobius.waila.plugin.neo.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.neo.NeoFluidData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;

public enum FluidCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(FluidData.class, res ->
            accessor.getTarget().getCapability(Capabilities.FLUID_HANDLER).ifPresent(handler -> {
                var size = handler.getTanks();
                var fluidData = NeoFluidData.of(size);

                for (var i = 0; i < size; i++) {
                    fluidData.add(handler.getFluidInTank(i), handler.getTankCapacity(i));
                }

                res.add(fluidData);
            }));
    }

}
