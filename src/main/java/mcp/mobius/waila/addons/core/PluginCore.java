package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraftforge.fluids.IFluidBlock;

@WailaPlugin
public class PluginCore implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        registrar.registerHeadProvider(HUDHandlerBlocks.INSTANCE, Block.class);
        registrar.registerTailProvider(HUDHandlerBlocks.INSTANCE, Block.class);

        // Fluid blocks using vanilla system (Vanilla)
        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, BlockLiquid.class);
        registrar.registerHeadProvider(HUDHandlerFluids.INSTANCE, BlockLiquid.class);
        registrar.registerTailProvider(HUDHandlerFluids.INSTANCE, BlockLiquid.class);
        // Fluid blocks using Forge's system (Mods doing things correctly)
        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, IFluidBlock.class);
        registrar.registerHeadProvider(HUDHandlerFluids.INSTANCE, IFluidBlock.class);
        registrar.registerTailProvider(HUDHandlerFluids.INSTANCE, IFluidBlock.class);

        registrar.registerHeadProvider(HUDHandlerEntities.INSTANCE, Entity.class);
        registrar.registerBodyProvider(HUDHandlerEntities.INSTANCE, Entity.class);
        registrar.registerTailProvider(HUDHandlerEntities.INSTANCE, Entity.class);

        registrar.addConfig("General", "general.showents");
        registrar.addConfig("General", "general.showhp");
        registrar.addConfig("General", "general.showcrop");
    }
}
