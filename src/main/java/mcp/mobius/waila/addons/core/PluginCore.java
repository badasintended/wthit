package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.TagLocation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraftforge.fluids.BlockFluidBase;

// This is treated as a normal plugin, but it is not loaded by the normal loader to make sure that it gets registered first.
public class PluginCore implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        registrar.registerProvider(HUDHandlerBlocks.INSTANCE, Block.class, TagLocation.HEAD, TagLocation.TAIL);

        // Fluid blocks using vanilla system (Vanilla)
        registrar.registerProvider(HUDHandlerFluids.INSTANCE, BlockLiquid.class, TagLocation.STACK, TagLocation.HEAD, TagLocation.TAIL);
        // Fluid blocks using Forge's system (Mods doing things correctly)
        registrar.registerProvider(HUDHandlerFluids.INSTANCE, BlockFluidBase.class, TagLocation.STACK, TagLocation.HEAD, TagLocation.TAIL);
        registrar.registerProvider(HUDHandlerEntities.INSTANCE, Entity.class, TagLocation.HEAD, TagLocation.BODY, TagLocation.TAIL);

        registrar.addConfig("General", "general.showents");
        registrar.addConfig("General", "general.showhp");
        registrar.addConfig("General", "general.showcrop");
    }
}
