package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaForgePlugin;
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

@WailaForgePlugin("waila:core")
public class PluginCore implements IWailaPlugin {

    static final Identifier RENDER_ENTITY_HEALTH = new Identifier(Waila.MODID, "render_health");

    public static final Identifier CONFIG_SHOW_BLOCK = new Identifier(Waila.MODID, "show_blocks");
    public static final Identifier CONFIG_SHOW_FLUID = new Identifier(Waila.MODID, "show_fluids");
    public static final Identifier CONFIG_SHOW_ENTITY = new Identifier(Waila.MODID, "show_entities");
    public static final Identifier CONFIG_SHOW_ITEM = new Identifier(Waila.MODID, "show_item");

    static final Identifier CONFIG_SHOW_MOD_NAME = new Identifier(Waila.MODID, "show_mod_name");
    static final Identifier CONFIG_SHOW_REGISTRY = new Identifier(Waila.MODID, "show_registry");
    static final Identifier CONFIG_SHOW_ENTITY_HEALTH = new Identifier(Waila.MODID, "show_entity_hp");
    static final Identifier CONFIG_SHOW_STATES = new Identifier(Waila.MODID, "show_states");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.HEAD, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.TAIL, Block.class);

        registrar.registerBlockDataProvider(HUDHandlerBlocks.INSTANCE, BlockEntity.class);

        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, FluidBlock.class);
        registrar.registerComponentProvider(HUDHandlerFluids.INSTANCE, TooltipPosition.HEAD, FluidBlock.class);

        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, AbstractMinecartEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, BoatEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.BODY, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, AbstractMinecartEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, BoatEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, FallingBlockEntity.class);

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