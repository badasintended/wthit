package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.config.Options;
import mcp.mobius.waila.plugin.core.event.CoreEventListener;
import mcp.mobius.waila.plugin.core.provider.BlockProvider;
import mcp.mobius.waila.plugin.core.provider.EntityProvider;
import mcp.mobius.waila.plugin.core.provider.FluidProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaCore implements IWailaPlugin {

    private static final int PRIORITY = 900;

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(BlockProvider.INSTANCE, HEAD, Block.class, PRIORITY);
        registrar.addComponent(BlockProvider.INSTANCE, BODY, Block.class, PRIORITY);
        registrar.addComponent(BlockProvider.INSTANCE, TAIL, Block.class, PRIORITY);

        registrar.addEventListener(CoreEventListener.INSTANCE, PRIORITY);

        registrar.addIcon(BlockProvider.INSTANCE, Block.class, 1100);
        registrar.addBlockData(BlockProvider.INSTANCE, BlockEntity.class);

        registrar.addIcon(FluidProvider.INSTANCE, LiquidBlock.class, PRIORITY);
        registrar.addComponent(FluidProvider.INSTANCE, HEAD, LiquidBlock.class, PRIORITY);

        registrar.addIcon(EntityProvider.INSTANCE, Entity.class, 1100);
        registrar.addComponent(EntityProvider.INSTANCE, HEAD, Entity.class, PRIORITY);
        registrar.addComponent(EntityProvider.INSTANCE, BODY, LivingEntity.class, PRIORITY);
        registrar.addComponent(EntityProvider.INSTANCE, TAIL, Entity.class, PRIORITY);

        registrar.addConfig(WailaConstants.CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_FLUID, false);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ICON, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(Options.STATES, false);
        registrar.addConfig(Options.POS, false);
    }

}
