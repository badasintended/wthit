package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.vanilla.renderer.ItemRenderer;
import mcp.mobius.waila.plugin.vanilla.renderer.ProgressRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;

public class WailaVanilla implements IWailaPlugin {

    // @formatter:off
    static final ResourceLocation RENDER_ITEM             = new ResourceLocation("item");
    static final ResourceLocation RENDER_FURNACE_PROGRESS = new ResourceLocation("furnace_progress");

    static final ResourceLocation CONFIG_DISPLAY_FURNACE    = new ResourceLocation("display_furnace_contents");
    static final ResourceLocation CONFIG_HIDE_SILVERFISH    = new ResourceLocation("hide_infestations");
    static final ResourceLocation CONFIG_HIDE_TRAPPED_CHEST = new ResourceLocation("hide_trapped_chest");
    static final ResourceLocation CONFIG_HIDE_POWDER_SNOW   = new ResourceLocation("hide_powder_snow");
    static final ResourceLocation CONFIG_SPAWNER_TYPE       = new ResourceLocation("spawner_type");
    static final ResourceLocation CONFIG_CROP_PROGRESS      = new ResourceLocation("crop_progress");
    static final ResourceLocation CONFIG_LEVER              = new ResourceLocation("lever");
    static final ResourceLocation CONFIG_REPEATER           = new ResourceLocation("repeater");
    static final ResourceLocation CONFIG_COMPARATOR         = new ResourceLocation("comparator");
    static final ResourceLocation CONFIG_REDSTONE           = new ResourceLocation("redstone");
    static final ResourceLocation CONFIG_JUKEBOX            = new ResourceLocation("jukebox");
    static final ResourceLocation CONFIG_PLAYER_HEAD_NAME   = new ResourceLocation("player_head_name");
    static final ResourceLocation CONFIG_COMPOSTER_LEVEL    = new ResourceLocation("composter_level");
    static final ResourceLocation CONFIG_HONEY_LEVEL        = new ResourceLocation("honey_level");
    static final ResourceLocation CONFIG_NOTE_BLOCK         = new ResourceLocation("note_block");
    static final ResourceLocation CONFIG_NOTE_BLOCK_FLAT    = new ResourceLocation("note_block_flat");
    // @formatter:on

    @Override
    public void register(IRegistrar registrar) {
        registrar.addSyncedConfig(CONFIG_HIDE_SILVERFISH, true);
        registrar.addSyncedConfig(CONFIG_HIDE_TRAPPED_CHEST, true);
        registrar.addSyncedConfig(CONFIG_HIDE_POWDER_SNOW, true);

        registrar.addConfig(CONFIG_DISPLAY_FURNACE, true);
        registrar.addConfig(CONFIG_SPAWNER_TYPE, true);
        registrar.addConfig(CONFIG_CROP_PROGRESS, true);
        registrar.addConfig(CONFIG_LEVER, true);
        registrar.addConfig(CONFIG_REPEATER, true);
        registrar.addConfig(CONFIG_COMPARATOR, true);
        registrar.addConfig(CONFIG_REDSTONE, true);
        registrar.addConfig(CONFIG_JUKEBOX, true);
        registrar.addConfig(CONFIG_PLAYER_HEAD_NAME, true);
        registrar.addConfig(CONFIG_COMPOSTER_LEVEL, true);
        registrar.addConfig(CONFIG_HONEY_LEVEL, true);
        registrar.addConfig(CONFIG_NOTE_BLOCK, true);
        registrar.addConfig(CONFIG_NOTE_BLOCK_FLAT, false);

        registrar.addRenderer(RENDER_ITEM, new ItemRenderer());
        registrar.addRenderer(RENDER_FURNACE_PROGRESS, new ProgressRenderer());

        registrar.addOverride(InfestedBlockComponent.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestComponent.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowComponent.INSTANCE, PowderSnowBlock.class);

        registrar.addDisplayItem(PlayerHeadComponent.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadComponent.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addComponent(SpawnerComponent.INSTANCE, HEAD, SpawnerBlockEntity.class, 999);

        registrar.addDisplayItem(PlantComponent.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantComponent.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneComponent.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addComponent(JukeboxComponent.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxComponent.INSTANCE, JukeboxBlockEntity.class);

        registrar.addComponent(FallingBlockComponent.INSTANCE, HEAD, FallingBlockEntity.class);
        registrar.addDisplayItem(FallingBlockComponent.INSTANCE, FallingBlockEntity.class);

        registrar.addDisplayItem(EntityIconComponent.INSTANCE, Entity.class);
        registrar.addDisplayItem(ItemEntityComponent.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityComponent.INSTANCE, HEAD, ItemEntity.class, 999);

        registrar.addComponent(FurnaceComponent.INSTANCE, BODY, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(FurnaceComponent.INSTANCE, AbstractFurnaceBlockEntity.class);

        registrar.addComponent(ComposterComponent.INSTANCE, BODY, ComposterBlock.class);

        registrar.addComponent(BeehiveComponent.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addComponent(NoteBlockComponent.INSTANCE, BODY, NoteBlock.class);
    }

}
