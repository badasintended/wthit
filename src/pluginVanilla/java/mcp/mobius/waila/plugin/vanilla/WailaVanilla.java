package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.config.Options.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BoatProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ComposterProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FallingBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FurnaceProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InfestedBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.NoteBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlantProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlayerHeadProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PowderSnowProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RedstoneProvider;
import mcp.mobius.waila.plugin.vanilla.provider.SpawnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.TrappedChestProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BeehiveBlock;
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
        registrar.addSyncedConfig(Options.ITEM_ENTITY, true);
        registrar.addSyncedConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addSyncedConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addSyncedConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addSyncedConfig(Options.PET_OWNER, true);

        registrar.addConfig(Options.FURNACE_CONTENTS, true);
        registrar.addConfig(Options.SPAWNER_TYPE, true);
        registrar.addConfig(Options.CROP_PROGRESS, true);
        registrar.addConfig(Options.REDSTONE_LEVER, true);
        registrar.addConfig(Options.REDSTONE_REPEATER, true);
        registrar.addConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addConfig(Options.REDSTONE_LEVEL, true);
        registrar.addConfig(Options.JUKEBOX_RECORD, true);
        registrar.addConfig(Options.PLAYER_HEAD_NAME, true);
        registrar.addConfig(Options.LEVEL_COMPOSTER, true);
        registrar.addConfig(Options.LEVEL_HONEY, true);
        registrar.addConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);

        registrar.addBlacklist(
            Blocks.BARRIER,
            Blocks.STRUCTURE_VOID);

        registrar.addBlacklist(
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.EXPERIENCE_ORB,
            EntityType.FIREBALL,
            EntityType.FIREWORK_ROCKET,
            EntityType.SNOWBALL);

        registrar.addOverride(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);

        registrar.addDisplayItem(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadProvider.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addComponent(SpawnerProvider.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addDisplayItem(PlantProvider.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SweetBerryBushBlock.class);

        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addComponent(JukeboxProvider.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.addComponent(FallingBlockProvider.INSTANCE, HEAD, FallingBlockEntity.class);
        registrar.addDisplayItem(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);

        registrar.addDisplayItem(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityProvider.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, TAIL, ItemEntity.class, 950);
        registrar.addOverride(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.addComponent(BoatProvider.INSTANCE, HEAD, Boat.class, 950);
        registrar.addComponent(BoatProvider.INSTANCE, TAIL, Boat.class, 950);

        registrar.addComponent(FurnaceProvider.INSTANCE, BODY, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(FurnaceProvider.INSTANCE, AbstractFurnaceBlockEntity.class);

        registrar.addComponent(ComposterProvider.INSTANCE, BODY, ComposterBlock.class);

        registrar.addComponent(BeehiveProvider.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addComponent(NoteBlockProvider.INSTANCE, BODY, NoteBlock.class);

        registrar.addComponent(PetOwnerProvider.INSTANCE, BODY, Entity.class);
    }

}
