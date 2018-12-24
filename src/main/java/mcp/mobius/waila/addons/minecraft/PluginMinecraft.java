package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererSpacer;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererStack;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.Identifier;

public class PluginMinecraft implements IWailaPlugin {

    private static final String MC_NAMESPACE = "minecraft";

    static final Identifier RENDER_ITEM = new Identifier(MC_NAMESPACE, "item");
    static final Identifier RENDER_SPACER = new Identifier(MC_NAMESPACE, "spacer");
    static final Identifier RENDER_FURNACE_PROGRESS = new Identifier(MC_NAMESPACE, "furnace_progress");

    static final Identifier CONFIG_DISPLAY_FURNACE = new Identifier(MC_NAMESPACE, "display_furnace_contents");
    static final Identifier CONFIG_HIDE_SILVERFISH = new Identifier(MC_NAMESPACE, "hide_infestations");
    static final Identifier CONFIG_SPAWNER_TYPE = new Identifier(MC_NAMESPACE, "spawner_type");
    static final Identifier CONFIG_CROP_PROGRESS = new Identifier(MC_NAMESPACE, "crop_progress");
    static final Identifier CONFIG_LEVER = new Identifier(MC_NAMESPACE, "lever");
    static final Identifier CONFIG_REPEATER = new Identifier(MC_NAMESPACE, "repeater");
    static final Identifier CONFIG_COMPARATOR = new Identifier(MC_NAMESPACE, "comparator");
    static final Identifier CONFIG_REDSTONE = new Identifier(MC_NAMESPACE, "redstone");
    static final Identifier CONFIG_JUKEBOX = new Identifier(MC_NAMESPACE, "jukebox");

    @Override
    public void register(IWailaRegistrar registrar) {
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

        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, StoneInfestedBlock.class);
        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, CropBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.HEAD, MobSpawnerBlockEntity.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, CropBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, StemBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, CocoaBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, LeverBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, RepeaterBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, ComparatorBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, RedstoneWireBlock.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, JukeboxBlockEntity.class);
        registrar.registerBlockDataProvider(HUDHandlerVanilla.INSTANCE, JukeboxBlockEntity.class);

        registrar.registerComponentProvider(HUDHandlerFurnace.INSTANCE, TooltipPosition.BODY, AbstractFurnaceBlockEntity.class);
        registrar.registerBlockDataProvider(HUDHandlerFurnace.INSTANCE, AbstractFurnaceBlockEntity.class);
    }
}
