package mcp.mobius.waila.addons.core;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererHealth;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;

public class PluginCore implements IWailaPlugin {

    static final ResourceLocation RENDER_ENTITY_HEALTH = new ResourceLocation(Waila.MODID, "render_health");

    static final ResourceLocation CONFIG_SHOW_REGISTRY = new ResourceLocation(Waila.MODID, "show_registry");
    public static final ResourceLocation CONFIG_SHOW_ENTITY = new ResourceLocation(Waila.MODID, "show_entities");
    static final ResourceLocation CONFIG_SHOW_ENTITY_HEALTH = new ResourceLocation(Waila.MODID, "show_entity_hp");
    static final ResourceLocation CONFIG_SHOW_STATES = new ResourceLocation(Waila.MODID, "show_states");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.HEAD, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.BODY, Block.class);
        registrar.registerComponentProvider(HUDHandlerBlocks.INSTANCE, TooltipPosition.TAIL, Block.class);

        registrar.registerStackProvider(HUDHandlerFluids.INSTANCE, BlockFlowingFluid.class);
        registrar.registerComponentProvider(HUDHandlerFluids.INSTANCE, TooltipPosition.HEAD, BlockFlowingFluid.class);

        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, EntityLiving.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, EntityHanging.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.HEAD, EntityMinecart.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.BODY, EntityLiving.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, EntityLiving.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, EntityHanging.class);
        registrar.registerComponentProvider(HUDHandlerEntities.INSTANCE, TooltipPosition.TAIL, EntityMinecart.class);

        registrar.addConfig(CONFIG_SHOW_REGISTRY, false);
        registrar.addConfig(CONFIG_SHOW_ENTITY, true);
        registrar.addConfig(CONFIG_SHOW_ENTITY_HEALTH, true);
        registrar.addConfig(CONFIG_SHOW_STATES, false);

        registrar.registerTooltipRenderer(RENDER_ENTITY_HEALTH, new TooltipRendererHealth());
    }
}
