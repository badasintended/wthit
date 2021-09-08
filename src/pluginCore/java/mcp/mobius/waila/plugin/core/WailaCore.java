package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.component.BlockComponent;
import mcp.mobius.waila.plugin.core.component.EntityComponent;
import mcp.mobius.waila.plugin.core.component.FluidComponent;
import mcp.mobius.waila.plugin.core.config.Options;
import mcp.mobius.waila.plugin.core.renderer.HealthRenderer;
import mcp.mobius.waila.plugin.core.renderer.Renderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaCore implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(BlockComponent.INSTANCE, HEAD, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, BODY, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, TAIL, Block.class, 900);

        registrar.addBlockData(BlockComponent.INSTANCE, BlockEntity.class);

        registrar.addDisplayItem(FluidComponent.INSTANCE, LiquidBlock.class);
        registrar.addComponent(FluidComponent.INSTANCE, HEAD, LiquidBlock.class, 900);

        registrar.addComponent(EntityComponent.INSTANCE, HEAD, Entity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, BODY, LivingEntity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, TAIL, Entity.class, 900);

        registrar.addConfig(WailaConstants.CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_FLUID, false);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ITEM, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(Options.ENTITY_HEALTH, true);
        registrar.addConfig(Options.STATES, false);
        registrar.addConfig(Options.POS, false);

        registrar.addRenderer(Renderers.RENDER_ENTITY_HEALTH, new HealthRenderer());
    }

}
