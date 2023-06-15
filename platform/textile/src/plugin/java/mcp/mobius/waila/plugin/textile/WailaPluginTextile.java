package mcp.mobius.waila.plugin.textile;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.data.BuiltinData;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.plugin.textile.fluid.TextileFluidDescriptor;
import mcp.mobius.waila.plugin.textile.provider.FluidStorageProvider;
import mcp.mobius.waila.plugin.textile.provider.ItemStorageProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

public abstract class WailaPluginTextile implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        BuiltinData.bootstrap(FluidData.class, ItemData.class);
        FluidData.describeFluid(Fluid.class, TextileFluidDescriptor.INSTANCE);
        FluidData.describeCauldron(Block.class, TextileFluidDescriptor.INSTANCE);
        registrar.addBlockData(ItemStorageProvider.INSTANCE, BlockEntity.class, 2000);
        registrar.addBlockData(FluidStorageProvider.INSTANCE, BlockEntity.class, 2000);
    }

}
