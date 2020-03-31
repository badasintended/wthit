package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererSpacer;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererStack;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;

public class PluginMinecraft implements IWailaPlugin {

    static final Identifier RENDER_ITEM = new Identifier("item");
    static final Identifier RENDER_SPACER = new Identifier("spacer");
    static final Identifier RENDER_FURNACE_PROGRESS = new Identifier("furnace_progress");

    static final Identifier CONFIG_DISPLAY_FURNACE = new Identifier("display_furnace_contents");
    static final Identifier CONFIG_HIDE_SILVERFISH = new Identifier("hide_infestations");
    static final Identifier CONFIG_SPAWNER_TYPE = new Identifier("spawner_type");
    static final Identifier CONFIG_CROP_PROGRESS = new Identifier("crop_progress");
    static final Identifier CONFIG_LEVER = new Identifier("lever");
    static final Identifier CONFIG_REPEATER = new Identifier("repeater");
    static final Identifier CONFIG_COMPARATOR = new Identifier("comparator");
    static final Identifier CONFIG_REDSTONE = new Identifier("redstone");
    static final Identifier CONFIG_JUKEBOX = new Identifier("jukebox");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(CONFIG_DISPLAY_FURNACE, true);
        registrar.addSyncedConfig(CONFIG_HIDE_SILVERFISH, true);
        registrar.addConfig(CONFIG_SPAWNER_TYPE, true);
        registrar.addConfig(CONFIG_CROP_PROGRESS, true);
        registrar.addConfig(CONFIG_LEVER, true);
        registrar.addConfig(CONFIG_REPEATER, true);
        registrar.addConfig(CONFIG_COMPARATOR, true);
        registrar.addConfig(CONFIG_REDSTONE, true);
        registrar.addConfig(CONFIG_JUKEBOX, true);

        registrar.registerTooltipRenderer(RENDER_ITEM, new TooltipRendererStack());
        registrar.registerTooltipRenderer(RENDER_SPACER, new TooltipRendererSpacer());
        registrar.registerTooltipRenderer(RENDER_FURNACE_PROGRESS, new TooltipRendererProgressBar());

        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, InfestedBlock.class);
        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, CropBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.HEAD, InfestedBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.HEAD, MobSpawnerBlockEntity.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, CropBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, StemBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, CocoaBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, SweetBerryBushBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, LeverBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, RepeaterBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, ComparatorBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, RedstoneWireBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, JukeboxBlockEntity.class);
        registrar.registerBlockDataProvider(HUDHandlerVanilla.INSTANCE, JukeboxBlockEntity.class);

        registrar.registerComponentProvider(HUDHandlerFallingBlock.INSTANCE, TooltipPosition.HEAD, FallingBlockEntity.class);
        registrar.registerComponentProvider(HUDHandlerFallingBlock.INSTANCE, TooltipPosition.TAIL, FallingBlockEntity.class);
        registrar.registerEntityStackProvider(HUDHandlerFallingBlock.INSTANCE, FallingBlockEntity.class);

        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, AbstractMinecartEntity.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, ItemFrameEntity.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, PaintingEntity.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, LeashKnotEntity.class);
        registrar.registerComponentProvider(HUDHandlerFurnace.INSTANCE, TooltipPosition.BODY, AbstractFurnaceBlockEntity.class);
        registrar.registerBlockDataProvider(HUDHandlerFurnace.INSTANCE, AbstractFurnaceBlockEntity.class);
    }
}
