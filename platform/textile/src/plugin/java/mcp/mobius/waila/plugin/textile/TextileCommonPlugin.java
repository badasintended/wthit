package mcp.mobius.waila.plugin.textile;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.plugin.textile.provider.FluidStorageProvider;
import mcp.mobius.waila.plugin.textile.provider.ItemStorageProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class TextileCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.blockData(ItemStorageProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.blockData(FluidStorageProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
