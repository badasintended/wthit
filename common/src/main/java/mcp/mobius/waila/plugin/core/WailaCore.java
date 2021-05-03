package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaCore implements IWailaPlugin {

    static final Identifier RENDER_ENTITY_HEALTH = Waila.id("render_health");

    public static final Identifier CONFIG_SHOW_BLOCK = Waila.id("show_blocks");
    public static final Identifier CONFIG_SHOW_FLUID = Waila.id("show_fluids");
    public static final Identifier CONFIG_SHOW_ENTITY = Waila.id("show_entities");
    public static final Identifier CONFIG_SHOW_ITEM = Waila.id("show_item");
    public static final Identifier CONFIG_SHOW_MOD_NAME = Waila.id("show_mod_name");

    static final Identifier CONFIG_SHOW_ENTITY_HEALTH = Waila.id("show_entity_hp");
    static final Identifier CONFIG_SHOW_STATES = Waila.id("show_states");
    static final Identifier CONFIG_SHOW_POS = Waila.id("show_pos");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addComponent(BlockComponent.INSTANCE, HEAD, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, BODY, Block.class, 900);
        registrar.addComponent(BlockComponent.INSTANCE, TAIL, Block.class, 1100);

        registrar.addBlockData(BlockComponent.INSTANCE, BlockEntity.class);

        registrar.addDisplayItem(FluidComponent.INSTANCE, FluidBlock.class);
        registrar.addComponent(FluidComponent.INSTANCE, HEAD, FluidBlock.class, 900);

        registrar.addComponent(EntityComponent.INSTANCE, HEAD, Entity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, BODY, LivingEntity.class, 900);
        registrar.addComponent(EntityComponent.INSTANCE, TAIL, Entity.class, 1100);

        registrar.addConfig(CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(CONFIG_SHOW_FLUID, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(CONFIG_SHOW_ITEM, true);
        registrar.addConfig(CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);
        registrar.addConfig(CONFIG_SHOW_POS, false);

        registrar.addRenderer(RENDER_ENTITY_HEALTH, new TooltipRendererHealth());
    }

}
