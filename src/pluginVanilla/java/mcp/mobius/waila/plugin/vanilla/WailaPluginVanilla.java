package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.api.data.FluidData;
import mcp.mobius.waila.plugin.vanilla.config.EnchantmentDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.fluid.LavaDescriptor;
import mcp.mobius.waila.plugin.vanilla.fluid.WaterDescriptor;
import mcp.mobius.waila.plugin.vanilla.provider.BaseContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeaconProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BlockAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BoatProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BreakProgressProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ChiseledBookShelfProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ComposterProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ContainerEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EnderChestProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EntityAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FallingBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.FurnaceProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HopperContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.HorseProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InfestedBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.InvisibleEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemEntityProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ItemFrameProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobEffectProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobTimerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.NoteBlockProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PandaProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlantProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PlayerHeadProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PowderSnowProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RandomizableContainerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.RedstoneProvider;
import mcp.mobius.waila.plugin.vanilla.provider.SpawnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.TrappedChestProvider;
import mcp.mobius.waila.plugin.vanilla.provider.VehicleProvider;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
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
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.material.Fluids;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class WailaPluginVanilla implements IWailaPlugin {

    @Override
    public void register(IRegistrar registrar) {
        registrar.addFeatureConfig(Options.ITEM_ENTITY, true);
        registrar.addIcon(ItemEntityProvider.INSTANCE, ItemEntity.class);
        registrar.addComponent(ItemEntityProvider.INSTANCE, HEAD, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, BODY, ItemEntity.class, 950);
        registrar.addComponent(ItemEntityProvider.INSTANCE, TAIL, ItemEntity.class, 950);
        registrar.addOverride(ItemEntityProvider.INSTANCE, ItemEntity.class);

        registrar.addFeatureConfig(Options.PET_OWNER, false);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);
        registrar.addComponent(PetOwnerProvider.INSTANCE, BODY, OwnableEntity.class);
        registrar.addEntityData(PetOwnerProvider.INSTANCE, OwnableEntity.class);

        registrar.addConfig(Options.ATTRIBUTE_BLOCK_POSITION, false);
        registrar.addConfig(Options.ATTRIBUTE_BLOCK_STATE, false);
        registrar.addConfig(Options.ATTRIBUTE_ENTITY_POSITION, false);
        registrar.addFeatureConfig(Options.ATTRIBUTE_HEALTH, true);
        registrar.addFeatureConfig(Options.ATTRIBUTE_ABSORPTION, false);
        registrar.addFeatureConfig(Options.ATTRIBUTE_ARMOR, true);
        registrar.addConfig(Options.ATTRIBUTE_COMPACT, false);
        registrar.addConfig(Options.ATTRIBUTE_ICON_PER_LINE, 25);
        registrar.addConfig(Options.ATTRIBUTE_LONG_HEALTH_MAX, 100);
        registrar.addConfig(Options.ATTRIBUTE_LONG_ARMOR_MAX, 100);
        registrar.addFeatureConfig(Options.ATTRIBUTE_HORSE_JUMP_HEIGHT, true);
        registrar.addFeatureConfig(Options.ATTRIBUTE_HORSE_SPEED, true);
        registrar.addFeatureConfig(Options.ATTRIBUTE_PANDA_GENES, true);
        registrar.addFeatureConfig(Options.ATTRIBUTE_BEACON_EFFECTS, false);
        registrar.addFeatureConfig(Options.ATTRIBUTE_MOB_EFFECTS, false);
        registrar.addSyncedConfig(Options.ATTRIBUTE_HIDDEN_MOB_EFFECTS, false, false);
        registrar.addComponent(BlockAttributesProvider.INSTANCE, BODY, Block.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, HEAD, Entity.class, 950);
        registrar.addComponent(EntityAttributesProvider.INSTANCE, BODY, Entity.class, 950);
        registrar.addComponent(HorseProvider.INSTANCE, BODY, AbstractHorse.class);
        registrar.addComponent(PandaProvider.INSTANCE, BODY, Panda.class);
        registrar.addComponent(BeaconProvider.INSTANCE, BODY, BeaconBlockEntity.class);
        registrar.addComponent(MobEffectProvider.INSTANCE, BODY, LivingEntity.class);
        registrar.addDataType(BeaconProvider.DATA, BeaconProvider.DATA_CODEC);
        registrar.addDataType(MobEffectProvider.DATA, MobEffectProvider.DATA_CODEC);
        registrar.addBlockData(BeaconProvider.INSTANCE, BeaconBlockEntity.class);
        registrar.addEntityData(EntityAttributesProvider.INSTANCE, Entity.class);
        registrar.addEntityData(MobEffectProvider.INSTANCE, LivingEntity.class);

        registrar.addFeatureConfig(Options.JUKEBOX_RECORD, false);
        registrar.addComponent(JukeboxProvider.INSTANCE, BODY, JukeboxBlockEntity.class);
        registrar.addBlockData(JukeboxProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.addFeatureConfig(Options.TIMER_GROW, false);
        registrar.addFeatureConfig(Options.TIMER_BREED, false);
        registrar.addComponent(MobTimerProvider.INSTANCE, BODY, AgeableMob.class);
        registrar.addEntityData(MobTimerProvider.INSTANCE, AgeableMob.class);

        registrar.addFeatureConfig(Options.OVERRIDE_INVISIBLE_ENTITY, true);
        registrar.addFeatureConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addFeatureConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addFeatureConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addConfig(Options.OVERRIDE_VEHICLE, true);
        registrar.addOverride(InvisibleEntityProvider.INSTANCE, LivingEntity.class);
        registrar.addOverride(InfestedBlockProvider.INSTANCE, InfestedBlock.class);
        registrar.addOverride(TrappedChestProvider.INSTANCE, TrappedChestBlock.class);
        registrar.addOverride(PowderSnowProvider.INSTANCE, PowderSnowBlock.class);
        registrar.addOverride(VehicleProvider.INSTANCE, Entity.class, 900);

        registrar.addFeatureConfig(Options.BREAKING_PROGRESS, true);
        registrar.addConfig(Options.BREAKING_PROGRESS_COLOR, 0xAAFFFFFF, IntFormat.ARGB_HEX);
        registrar.addConfig(Options.BREAKING_PROGRESS_BOTTOM_ONLY, false);
        registrar.addEventListener(BreakProgressProvider.INSTANCE);

        registrar.addFeatureConfig(Options.SPAWNER_TYPE, true);
        registrar.addComponent(SpawnerProvider.INSTANCE, HEAD, SpawnerBlockEntity.class, 950);

        registrar.addFeatureConfig(Options.CROP_PROGRESS, true);
        registrar.addFeatureConfig(Options.CROP_GROWABLE, true);
        registrar.addFeatureConfig(Options.TREE_GROWABLE, true);
        registrar.addIcon(PlantProvider.INSTANCE, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CropBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, StemBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, CocoaBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, NetherWartBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SweetBerryBushBlock.class);
        registrar.addComponent(PlantProvider.INSTANCE, BODY, SaplingBlock.class);

        registrar.addFeatureConfig(Options.REDSTONE_LEVER, true);
        registrar.addFeatureConfig(Options.REDSTONE_REPEATER, true);
        registrar.addFeatureConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addFeatureConfig(Options.REDSTONE_LEVEL, true);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, LeverBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RepeaterBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, ComparatorBlock.class);
        registrar.addComponent(RedstoneProvider.INSTANCE, BODY, RedStoneWireBlock.class);

        registrar.addFeatureConfig(Options.PLAYER_HEAD_NAME, true);
        registrar.addIcon(PlayerHeadProvider.INSTANCE, SkullBlockEntity.class);
        registrar.addComponent(PlayerHeadProvider.INSTANCE, BODY, SkullBlockEntity.class);

        registrar.addFeatureConfig(Options.LEVEL_COMPOSTER, true);
        registrar.addFeatureConfig(Options.LEVEL_HONEY, true);
        registrar.addComponent(ComposterProvider.INSTANCE, BODY, ComposterBlock.class);
        registrar.addComponent(BeehiveProvider.INSTANCE, BODY, BeehiveBlock.class);

        registrar.addFeatureConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);
        registrar.addComponent(NoteBlockProvider.INSTANCE, BODY, NoteBlock.class);

        registrar.addIcon(FallingBlockProvider.INSTANCE, FallingBlockEntity.class);
        registrar.addComponent(FallingBlockProvider.INSTANCE, HEAD, FallingBlockEntity.class);

        registrar.addComponent(BoatProvider.INSTANCE, HEAD, Boat.class, 950);
        registrar.addComponent(BoatProvider.INSTANCE, TAIL, Boat.class, 950);

        registrar.addIcon(ItemFrameProvider.INSTANCE, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, HEAD, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, BODY, ItemFrame.class);
        registrar.addComponent(ItemFrameProvider.INSTANCE, TAIL, ItemFrame.class);

        registrar.addFeatureConfig(Options.BOOK_BOOKSHELF, false);
        registrar.addConfig(Options.BOOK_ENCHANTMENT_DISPLAY_MODE, EnchantmentDisplayMode.CYCLE);
        registrar.addConfig(Options.BOOK_ENCHANTMENT_CYCLE_TIMING, 500);
        registrar.addConfig(Options.BOOK_WRITTEN, true);
        registrar.addDataType(ChiseledBookShelfProvider.DATA, ChiseledBookShelfProvider.DATA_CODEC);
        registrar.addIcon(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, HEAD, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, BODY, ChiseledBookShelfBlock.class);
        registrar.addComponent(ChiseledBookShelfProvider.INSTANCE, TAIL, ChiseledBookShelfBlock.class);
        registrar.addBlockData(ChiseledBookShelfProvider.INSTANCE, ChiseledBookShelfBlockEntity.class);

        FluidData.describeFluid(Fluids.WATER, WaterDescriptor.INSTANCE);
        FluidData.describeFluid(Fluids.LAVA, LavaDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.WATER_CAULDRON, WaterDescriptor.INSTANCE);
        FluidData.describeCauldron(Blocks.LAVA_CAULDRON, LavaDescriptor.INSTANCE);

        registrar.addBlockData(FurnaceProvider.INSTANCE, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(EnderChestProvider.INSTANCE, EnderChestBlockEntity.class);

        registrar.addBlockData(RandomizableContainerProvider.INSTANCE, RandomizableContainer.class, 1100);
        registrar.addBlockData(BaseContainerProvider.INSTANCE, BaseContainerBlockEntity.class, 1200);
        registrar.addBlockData(HopperContainerProvider.INSTANCE, BlockEntity.class, 1300);

        registrar.addEntityData(ContainerEntityProvider.INSTANCE, Container.class, 1100);

        registrar.addBlacklist(
            Blocks.BARRIER,
            Blocks.STRUCTURE_VOID);

        registrar.addBlacklist(
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.EXPERIENCE_ORB,
            EntityType.FIREBALL,
            EntityType.FIREWORK_ROCKET,
            EntityType.INTERACTION,
            EntityType.SNOWBALL);
    }

}
