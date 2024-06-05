package mcp.mobius.waila.plugin.forge.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.EnergyData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public enum EnergyCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(EnergyData.TYPE, res ->
            accessor.getTarget().getCapability(ForgeCapabilities.ENERGY).ifPresent(storage ->
                res.add(EnergyData.of(storage.getEnergyStored(), storage.getMaxEnergyStored()))));
    }

}
