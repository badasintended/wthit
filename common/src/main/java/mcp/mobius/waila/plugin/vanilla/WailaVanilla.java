package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererStack;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;

public class WailaVanilla implements IWailaPlugin {

    static final Identifier RENDER_ITEM = new Identifier("item");
    static final Identifier RENDER_FURNACE_PROGRESS = new Identifier("furnace_progress");

    static final Identifier CONFIG_DISPLAY_FURNACE = new Identifier("display_furnace_contents");
    static final Identifier CONFIG_HIDE_SILVERFISH = new Identifier("hide_infestations");
    static final Identifier CONFIG_HIDE_TRAPPED_CHEST = new Identifier("hide_trapped_chest");
    static final Identifier CONFIG_SPAWNER_TYPE = new Identifier("spawner_type");
    static final Identifier CONFIG_CROP_PROGRESS = new Identifier("crop_progress");
    static final Identifier CONFIG_LEVER = new Identifier("lever");
    static final Identifier CONFIG_REPEATER = new Identifier("repeater");
    static final Identifier CONFIG_COMPARATOR = new Identifier("comparator");
    static final Identifier CONFIG_REDSTONE = new Identifier("redstone");
    static final Identifier CONFIG_JUKEBOX = new Identifier("jukebox");
    static final Identifier CONFIG_PLAYER_HEAD_NAME = new Identifier("player_head_name");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addSyncedConfig(CONFIG_HIDE_SILVERFISH, true);
        registrar.addSyncedConfig(CONFIG_HIDE_TRAPPED_CHEST, true);
        registrar.addConfig(CONFIG_DISPLAY_FURNACE, true);
        registrar.addConfig(CONFIG_SPAWNER_TYPE, true);
        registrar.addConfig(CONFIG_CROP_PROGRESS, true);
        registrar.addConfig(CONFIG_LEVER, true);
        registrar.addConfig(CONFIG_REPEATER, true);
        registrar.addConfig(CONFIG_COMPARATOR, true);
        registrar.addConfig(CONFIG_REDSTONE, true);
        registrar.addConfig(CONFIG_JUKEBOX, true);
        registrar.addConfig(CONFIG_PLAYER_HEAD_NAME, true);

        registrar.addRenderer(RENDER_ITEM, new TooltipRendererStack());
        registrar.addRenderer(RENDER_FURNACE_PROGRESS, new TooltipRendererProgressBar());

        registrar.addOverride(InfestedBlockComponent.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestComponent.INSTANCE, TrappedChestBlock.class);

        registrar.addDisplayItem(PlayerHeadComponent.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadComponent.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addComponent(SpawnerComponent.INSTANCE, HEAD, MobSpawnerBlockEntity.class, 999);

        registrar.addDisplayItem(PlantComponent.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RedstoneWireBlock.class);

        registrar.addComponent(JukeboxComponent.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxComponent.INSTANCE, JukeboxBlockEntity.class);

        registrar.addComponent(FallingBlockComponent.INSTANCE, HEAD, FallingBlockEntity.class);
        registrar.addDisplayItem(FallingBlockComponent.INSTANCE, FallingBlockEntity.class);

        registrar.addDisplayItem(EntityIconComponent.INSTANCE, Entity.class);

        registrar.addComponent(FurnaceComponent.INSTANCE, BODY, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(FurnaceComponent.INSTANCE, AbstractFurnaceBlockEntity.class);
    }

}
