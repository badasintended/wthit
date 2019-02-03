package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;

public class PluginCore implements IWailaPlugin {

    static final Identifier RENDER_ENTITY_HEALTH = new Identifier(Waila.MODID, "render_health");

    static final Identifier CONFIG_SHOW_REGISTRY = new Identifier(Waila.MODID, "show_registry");
    public static final Identifier CONFIG_SHOW_ENTITY = new Identifier(Waila.MODID, "show_entities");
    static final Identifier CONFIG_SHOW_ENTITY_HEALTH = new Identifier(Waila.MODID, "show_entity_hp");
    static final Identifier CONFIG_SHOW_STATES = new Identifier(Waila.MODID, "show_states");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.HEAD, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.TAIL, Block.class);

        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, FluidBlock.class);
        registrar.registerComponentProvider(HUDHandlerFluids.INSTANCE, TooltipPosition.HEAD, FluidBlock.class);

        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, AbstractMinecartEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.BODY, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, LivingEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, AbstractDecorationEntity.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, AbstractMinecartEntity.class);

        registrar.addConfig(CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);

        registrar.registerTooltipRenderer(RENDER_ENTITY_HEALTH, new TooltipRendererHealth());
    }
}
