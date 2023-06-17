package mcp.mobius.waila.plugin.trenergy;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.trenergy.provider.EnergyStorageProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WailaPluginTeamRebornEnergy implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addBlockData(EnergyStorageProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
