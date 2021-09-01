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

    static final ResourceLocation CONFIG_DISPLAY_FURNACE        = new ResourceLocation("furnace_contents");
    static final ResourceLocation CONFIG_OVERRIDE_INFESTED      = new ResourceLocation("override.infested");
    static final ResourceLocation CONFIG_OVERRIDE_TRAPPED_CHEST = new ResourceLocation("override.trapped_chest");
    static final ResourceLocation CONFIG_OVERRIDE_POWDER_SNOW   = new ResourceLocation("override.powder_snow");
    static final ResourceLocation CONFIG_SPAWNER_TYPE           = new ResourceLocation("spawner_type");
    static final ResourceLocation CONFIG_CROP_PROGRESS          = new ResourceLocation("crop_progress");
    static final ResourceLocation CONFIG_REDSTONE_LEVER         = new ResourceLocation("redstone.lever");
    static final ResourceLocation CONFIG_REDSTONE_REPEATER      = new ResourceLocation("redstone.repeater");
    static final ResourceLocation CONFIG_REDSTONE_COMPARATOR    = new ResourceLocation("redstone.comparator");
    static final ResourceLocation CONFIG_REDSTONE_LEVEL         = new ResourceLocation("redstone.level");
    static final ResourceLocation CONFIG_JUKEBOX_RECORD         = new ResourceLocation("jukebox.record");
    static final ResourceLocation CONFIG_PLAYER_HEAD_NAME       = new ResourceLocation("player_head.name");
    static final ResourceLocation CONFIG_LEVEL_COMPOSTER        = new ResourceLocation("level.composter");
    static final ResourceLocation CONFIG_LEVEL_HONEY            = new ResourceLocation("level.honey");
    static final ResourceLocation CONFIG_NOTE_BLOCK_TYPE        = new ResourceLocation("note_block.type");
    static final ResourceLocation CONFIG_NOTE_BLOCK_FLAT        = new ResourceLocation("note_block.flat");
    // @formatter:on

    @Override
    public void register(IRegistrar registrar) {
        registrar.addSyncedConfig(CONFIG_OVERRIDE_INFESTED, true);
        registrar.addSyncedConfig(CONFIG_OVERRIDE_TRAPPED_CHEST, true);
        registrar.addSyncedConfig(CONFIG_OVERRIDE_POWDER_SNOW, true);

        registrar.addConfig(CONFIG_DISPLAY_FURNACE, true);
        registrar.addConfig(CONFIG_SPAWNER_TYPE, true);
        registrar.addConfig(CONFIG_CROP_PROGRESS, true);
        registrar.addConfig(CONFIG_REDSTONE_LEVER, true);
        registrar.addConfig(CONFIG_REDSTONE_REPEATER, true);
        registrar.addConfig(CONFIG_REDSTONE_COMPARATOR, true);
        registrar.addConfig(CONFIG_REDSTONE_LEVEL, true);
        registrar.addConfig(CONFIG_JUKEBOX_RECORD, true);
        registrar.addConfig(CONFIG_PLAYER_HEAD_NAME, true);
        registrar.addConfig(CONFIG_LEVEL_COMPOSTER, true);
        registrar.addConfig(CONFIG_LEVEL_HONEY, true);
        registrar.addConfig(CONFIG_NOTE_BLOCK_TYPE, true);
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
