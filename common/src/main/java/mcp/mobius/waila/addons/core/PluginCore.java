package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class PluginCore implements IWailaPlugin {

    static final Identifier RENDER_ENTITY_HEALTH = Waila.id("render_health");

    public static final Identifier CONFIG_SHOW_BLOCK = Waila.id("show_blocks");
    public static final Identifier CONFIG_SHOW_FLUID = Waila.id("show_fluids");
    public static final Identifier CONFIG_SHOW_ENTITY = Waila.id("show_entities");
    public static final Identifier CONFIG_SHOW_ITEM = Waila.id("show_item");

    static final Identifier CONFIG_SHOW_MOD_NAME = Waila.id("show_mod_name");
    static final Identifier CONFIG_SHOW_REGISTRY = Waila.id("show_registry");
    static final Identifier CONFIG_SHOW_ENTITY_HEALTH = Waila.id("show_entity_hp");
    static final Identifier CONFIG_SHOW_STATES = Waila.id("show_states");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(999, HUDHandlerBlocks.INSTANCE, HEAD, Block.class);
        registrar.registerComponentProvider(999, HUDHandlerBlocks.INSTANCE, BODY, Block.class);
        registrar.registerComponentProvider(Integer.MAX_VALUE, HUDHandlerBlocks.INSTANCE, TAIL, Block.class);

        registrar.registerBlockDataProvider(HUDHandlerBlocks.INSTANCE, BlockEntity.class);

        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, FluidBlock.class);
        registrar.registerComponentProvider(999, HUDHandlerFluids.INSTANCE, HEAD, FluidBlock.class);

        registrar.registerComponentProvider(999, HUDHandlerEntities.INSTANCE, HEAD, LivingEntity.class);
        registrar.registerComponentProvider(999, HUDHandlerEntities.INSTANCE, HEAD, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(999, HUDHandlerEntities.INSTANCE, HEAD, AbstractMinecartEntity.class);
        registrar.registerComponentProvider(999, HUDHandlerEntities.INSTANCE, HEAD, BoatEntity.class);
        registrar.registerComponentProvider(999, HUDHandlerEntities.INSTANCE, BODY, LivingEntity.class);
        registrar.registerComponentProvider(1001, HUDHandlerEntities.INSTANCE, TAIL, LivingEntity.class);
        registrar.registerComponentProvider(1001, HUDHandlerEntities.INSTANCE, TAIL, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(1001, HUDHandlerEntities.INSTANCE, TAIL, AbstractMinecartEntity.class);
        registrar.registerComponentProvider(1001, HUDHandlerEntities.INSTANCE, TAIL, BoatEntity.class);
        registrar.registerComponentProvider(1001, HUDHandlerEntities.INSTANCE, TAIL, FallingBlockEntity.class);

        registrar.addConfig(CONFIG_SHOW_BLOCK, true);
        registrar.addConfig(CONFIG_SHOW_FLUID, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(CONFIG_SHOW_ITEM, true);
        registrar.addConfig(CONFIG_SHOW_MOD_NAME, true);
        registrar.addConfig(CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);

        registrar.registerTooltipRenderer(RENDER_ENTITY_HEALTH, new TooltipRendererHealth());
    }

}
