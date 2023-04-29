package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BlockAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BoatProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BreakProgressProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ComposterProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EntityAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FallingBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FurnaceProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HorseProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InfestedBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemFrameProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobTimerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.NoteBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlantProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlayerHeadProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PowderSnowProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RedstoneProvider;
import mcp.mobius.waila.plugin.vanilla.provider.SpawnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.TrappedChestProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaVanilla implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addMergedConfig(Options.ITEM_ENTITY, true);
        registrar.addIcon(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityProvider.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, TAIL, ItemEntity.class, 950);
        registrar.addOverride(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.addMergedConfig(Options.PET_OWNER, true);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);
        registrar.addComponent(PetOwnerProvider.INSTANCE, BODY, Entity.class);

        registrar.addConfig(Options.ATTRIBUTE_BLOCK_POSITION, false);
        registrar.addConfig(Options.ATTRIBUTE_BLOCK_STATE, false);
        registrar.addConfig(Options.ATTRIBUTE_ENTITY_POSITION, false);
        registrar.addMergedConfig(Options.ATTRIBUTE_HEALTH, true);
        registrar.addMergedSyncedConfig(Options.ATTRIBUTE_ABSORPTION, true, false);
        registrar.addMergedConfig(Options.ATTRIBUTE_ARMOR, true);
        registrar.addConfig(Options.ATTRIBUTE_COMPACT, false);
        registrar.addConfig(Options.ATTRIBUTE_ICON_PER_LINE, 25);
        registrar.addConfig(Options.ATTRIBUTE_LONG_HEALTH_MAX, 100);
        registrar.addConfig(Options.ATTRIBUTE_LONG_ARMOR_MAX, 100);
        registrar.addMergedConfig(Options.ATTRUBUTE_HORSE_JUMP_HEIGHT, true);
        registrar.addMergedConfig(Options.ATTRUBUTE_HORSE_SPEED, true);
        registrar.addComponent(BlockAttributesProvider.INSTANCE, BODY, Block.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, HEAD, Entity.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, BODY, Entity.class, 950);
        registrar.addComponent(HorseProvider.INSTANCE, BODY, AbstractHorse.class);
        registrar.addEntityData(EntityAttributesProvider.INSTANCE, Entity.class);

        registrar.addMergedSyncedConfig(Options.FURNACE_CONTENTS, true, false);
        registrar.addComponent(FurnaceProvider.INSTANCE, BODY, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(FurnaceProvider.INSTANCE, AbstractFurnaceBlockEntity.class);

        registrar.addMergedSyncedConfig(Options.JUKEBOX_RECORD, true, false);
        registrar.addComponent(JukeboxProvider.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.addMergedSyncedConfig(Options.TIMER_GROW, true, false);
        registrar.addMergedSyncedConfig(Options.TIMER_BREED, true, false);
        registrar.addComponent(MobTimerProvider.INSTANCE, BODY, AgeableMob.class);
        registrar.addEntityData(MobTimerProvider.INSTANCE, AgeableMob.class);

        registrar.addMergedConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addMergedConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addMergedConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addOverride(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);

        registrar.addMergedConfig(Options.BREAKING_PROGRESS, true);
        registrar.addConfig(Options.BREAKING_PROGRESS_COLOR, 0xAAFFFFFF, IntFormat.ARGB_HEX);
        registrar.addConfig(Options.BREAKING_PROGRESS_BOTTOM_ONLY, false);
        registrar.addEventListener(BreakProgressProvider.INSTANCE);

        registrar.addMergedConfig(Options.SPAWNER_TYPE, true);
        registrar.addComponent(SpawnerProvider.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addMergedConfig(Options.CROP_PROGRESS, true);
        registrar.addIcon(PlantProvider.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addMergedConfig(Options.REDSTONE_LEVER, true);
        registrar.addMergedConfig(Options.REDSTONE_REPEATER, true);
        registrar.addMergedConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addMergedConfig(Options.REDSTONE_LEVEL, true);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addMergedConfig(Options.PLAYER_HEAD_NAME, true);
        registrar.addIcon(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadProvider.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addMergedConfig(Options.LEVEL_COMPOSTER, true);
        registrar.addMergedConfig(Options.LEVEL_HONEY, true);
        registrar.addComponent(ComposterProvider.INSTANCE, BODY, ComposterBlock.class);
        registrar.addComponent(BeehiveProvider.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addMergedConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);
        registrar.addComponent(NoteBlockProvider.INSTANCE, BODY, NoteBlock.class);

        registrar.addIcon(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);
        registrar.addComponent(FallingBlockProvider.INSTANCE, HEAD, FallingBlockEntity.class);

        registrar.addComponent(BoatProvider.INSTANCE, HEAD, Boat.class, 950);
        registrar.addComponent(BoatProvider.INSTANCE, TAIL, Boat.class, 950);

        registrar.addIcon(ItemFrameProvider.INSTANCE, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, HEAD, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, TAIL, ItemFrame.class);

        registrar.addBlacklist(
            Blocks.BARRIER,
            Blocks.STRUCTURE_VOID);

        registrar.addBlacklist(
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.EXPERIENCE_ORB,
            EntityType.FIREBALL,
            EntityType.FIREWORK_ROCKET,
            EntityType.SNOWBALL);
    }

}
