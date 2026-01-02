package mcp.mobius.waila.plugin.trenergy;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.trenergy.provider.EnergyStorageProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TeamRebornEnergyCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.blockData(EnergyStorageProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
