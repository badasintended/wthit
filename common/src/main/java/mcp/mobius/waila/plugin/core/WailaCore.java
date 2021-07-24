package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.renderer.HealthRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaCore implements IWailaPlugin {

    // @formatter:off
    static final ResourceLocation RENDER_ENTITY_HEALTH = Waila.id("render_health");

    static final ResourceLocation CONFIG_SHOW_ENTITY_HEALTH = Waila.id("show_entity_hp");
    static final ResourceLocation CONFIG_SHOW_STATES        = Waila.id("show_states");
    static final ResourceLocation CONFIG_SHOW_POS           = Waila.id("show_pos");
    // @formatter:on

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(BlockComponent.INSTANCE, HEAD, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, BODY, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, TAIL, Block.class, 1100);

        registrar.addBlockData(BlockComponent.INSTANCE, BlockEntity.class);

        registrar.addDisplayItem(FluidComponent.INSTANCE, LiquidBlock.class);
        registrar.addComponent(FluidComponent.INSTANCE, HEAD, LiquidBlock.class, 900);

        registrar.addComponent(EntityComponent.INSTANCE, HEAD, Entity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, BODY, LivingEntity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, TAIL, Entity.class, 1100);

        registrar.addConfig(WailaConstants.CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_FLUID, false);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_ITEM, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(WailaConstants.CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);
        registrar.addConfig(CONFIG_SHOW_POS, false);

        registrar.addRenderer(RENDER_ENTITY_HEALTH, new HealthRenderer());
    }

}
