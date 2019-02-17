package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererSpacer;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererStack;
import net.minecraft.block.*;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityJukebox;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;

public class PluginMinecraft implements IWailaPlugin {

    static final ResourceLocation RENDER_ITEM = new ResourceLocation("item");
    static final ResourceLocation RENDER_SPACER = new ResourceLocation("spacer");
    static final ResourceLocation RENDER_FURNACE_PROGRESS = new ResourceLocation("furnace_progress");

    static final ResourceLocation CONFIG_DISPLAY_FURNACE = new ResourceLocation("display_furnace_contents");
    static final ResourceLocation CONFIG_HIDE_SILVERFISH = new ResourceLocation("hide_infestations");
    static final ResourceLocation CONFIG_SPAWNER_TYPE = new ResourceLocation("spawner_type");
    static final ResourceLocation CONFIG_CROP_PROGRESS = new ResourceLocation("crop_progress");
    static final ResourceLocation CONFIG_LEVER = new ResourceLocation("lever");
    static final ResourceLocation CONFIG_REPEATER = new ResourceLocation("repeater");
    static final ResourceLocation CONFIG_COMPARATOR = new ResourceLocation("comparator");
    static final ResourceLocation CONFIG_REDSTONE = new ResourceLocation("redstone");
    static final ResourceLocation CONFIG_JUKEBOX = new ResourceLocation("jukebox");

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

        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, BlockSilverfish.class);
        registrar.registerStackProvider(HUDHandlerVanilla.INSTANCE, BlockCrops.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.HEAD, TileEntityMobSpawner.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockCrops.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockStem.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockCocoa.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockLever.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockRedstoneRepeater.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockRedstoneComparator.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, BlockRedstone.class);
        registrar.registerComponentProvider(HUDHandlerVanilla.INSTANCE, TooltipPosition.BODY, TileEntityJukebox.class);
        registrar.registerBlockDataProvider(HUDHandlerVanilla.INSTANCE, TileEntityJukebox.class);

        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, EntityMinecart.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, EntityItemFrame.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, EntityPainting.class);
        registrar.registerEntityStackProvider(HUDHandlerEntityIcon.INSTANCE, EntityLeashKnot.class);
        registrar.registerComponentProvider(HUDHandlerFurnace.INSTANCE, TooltipPosition.BODY, TileEntityFurnace.class);
        registrar.registerBlockDataProvider(HUDHandlerFurnace.INSTANCE, TileEntityFurnace.class);
    }
}
