package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.plugin.vanilla.config.EnchantmentDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.BeaconProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeeProvider;
import mcp.mobius.waila.plugin.vanilla.provider.BeehiveProvider;
import mcp.mobius.waila.plugin.vanilla.provider.ChiseledBookShelfProvider;
import mcp.mobius.waila.plugin.vanilla.provider.EntityAttributesProvider;
import mcp.mobius.waila.plugin.vanilla.provider.JukeboxProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobEffectProvider;
import mcp.mobius.waila.plugin.vanilla.provider.MobTimerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.PetOwnerProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.BaseContainerDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeaconDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeeDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeehiveDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.ChiseledBookShelfDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.ContainerEntityDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.EnderChestDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.EntityAttributesDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.FurnaceDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.HopperContainerDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.JukeboxDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.MobEffectDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.MobTimerDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.PetOwnerDataProvider;
import mcp.mobius.waila.plugin.vanilla.provider.data.RandomizableContainerDataProvider;
import net.minecraft.world.Container;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public class VanillaCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.addConfig(Options.BLOCK_POSITION, false);
        registrar.addConfig(Options.BLOCK_STATE, false);

        registrar.addFeatureConfig(Options.ENTITY_ITEM_ENTITY, true);

        registrar.addConfig(Options.ENTITY_POSITION, false);
        registrar.addFeatureConfig(Options.ENTITY_HEALTH, true);
        registrar.addFeatureConfig(Options.ENTITY_ABSORPTION, false);
        registrar.addFeatureConfig(Options.ENTITY_ARMOR, true);
        registrar.addConfig(Options.ENTITY_COMPACT, false);
        registrar.addConfig(Options.ENTITY_ICON_PER_LINE, 25);
        registrar.addConfig(Options.ENTITY_LONG_HEALTH_MAX, 100);
        registrar.addConfig(Options.ENTITY_LONG_ARMOR_MAX, 100);
        registrar.addEntityData(EntityAttributesDataProvider.INSTANCE, Entity.class);

        registrar.addFeatureConfig(Options.PET_OWNER, false);
        registrar.addConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);
        registrar.addEntityData(PetOwnerDataProvider.INSTANCE, OwnableEntity.class);

        registrar.addFeatureConfig(Options.HORSE_JUMP_HEIGHT, true);
        registrar.addFeatureConfig(Options.HORSE_SPEED, true);

        registrar.addFeatureConfig(Options.PANDA_GENES, true);

        registrar.addFeatureConfig(Options.BEE_HIVE_POS, false);
        registrar.addFeatureConfig(Options.BEE_HIVE_HONEY_LEVEL, true);
        registrar.addFeatureConfig(Options.BEE_HIVE_OCCUPANTS, false);
        registrar.addDataType(BeehiveDataProvider.OCCUPANTS, BeehiveDataProvider.OccupantsData.class, BeehiveDataProvider.OccupantsData::new);
        registrar.addDataType(BeeDataProvider.HIVE_POS, BeeDataProvider.HivePosData.class, BeeDataProvider.HivePosData::new);
        registrar.addBlockData(BeehiveDataProvider.INSTANCE, BeehiveBlockEntity.class);
        registrar.addEntityData(BeeDataProvider.INSTANCE, Bee.class);

        registrar.addFeatureConfig(Options.EFFECT_BEACON, false);
        registrar.addDataType(BeaconDataProvider.DATA, BeaconDataProvider.Data.class, BeaconDataProvider.Data::new);
        registrar.addBlockData(BeaconDataProvider.INSTANCE, BeaconBlockEntity.class);

        registrar.addFeatureConfig(Options.EFFECT_MOB, false);
        registrar.addSyncedConfig(Options.EFFECT_HIDDEN_MOB, false, false);
        registrar.addDataType(MobEffectDataProvider.DATA, MobEffectDataProvider.Data.class, MobEffectDataProvider.Data::new);
        registrar.addEntityData(MobEffectDataProvider.INSTANCE, LivingEntity.class);

        registrar.addFeatureConfig(Options.JUKEBOX_RECORD, false);
        registrar.addBlockData(JukeboxDataProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.addFeatureConfig(Options.TIMER_GROW, false);
        registrar.addFeatureConfig(Options.TIMER_BREED, false);
        registrar.addEntityData(MobTimerDataProvider.INSTANCE, AgeableMob.class);

        registrar.addFeatureConfig(Options.OVERRIDE_INVISIBLE_ENTITY, true);
        registrar.addFeatureConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.addFeatureConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.addFeatureConfig(Options.OVERRIDE_INFESTED, true);
        registrar.addConfig(Options.OVERRIDE_VEHICLE, true);

        registrar.addFeatureConfig(Options.BREAKING_PROGRESS, true);
        registrar.addConfig(Options.BREAKING_PROGRESS_COLOR, 0xAAFFFFFF, IntFormat.ARGB_HEX);
        registrar.addConfig(Options.BREAKING_PROGRESS_BOTTOM_ONLY, false);

        registrar.addFeatureConfig(Options.SPAWNER_TYPE, true);

        registrar.addFeatureConfig(Options.PLANT_CROP_PROGRESS, true);
        registrar.addFeatureConfig(Options.PLANT_CROP_GROWABLE, true);
        registrar.addFeatureConfig(Options.PLANT_TREE_GROWABLE, true);

        registrar.addFeatureConfig(Options.REDSTONE_LEVER, true);
        registrar.addFeatureConfig(Options.REDSTONE_REPEATER, true);
        registrar.addFeatureConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.addFeatureConfig(Options.REDSTONE_LEVEL, true);

        registrar.addFeatureConfig(Options.PLAYER_HEAD_NAME, true);

        registrar.addFeatureConfig(Options.LEVEL_COMPOSTER, true);

        registrar.addFeatureConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.addConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.addConfig(Options.NOTE_BLOCK_INT_VALUE, false);

        registrar.addFeatureConfig(Options.BOOK_BOOKSHELF, false);
        registrar.addConfig(Options.BOOK_ENCHANTMENT_DISPLAY_MODE, EnchantmentDisplayMode.CYCLE);
        registrar.addConfig(Options.BOOK_ENCHANTMENT_CYCLE_TIMING, 500);
        registrar.addConfig(Options.BOOK_WRITTEN, true);
        registrar.addDataType(ChiseledBookShelfDataProvider.DATA, ChiseledBookShelfDataProvider.Data.class, ChiseledBookShelfDataProvider.Data::new);
        registrar.addBlockData(ChiseledBookShelfDataProvider.INSTANCE, ChiseledBookShelfBlockEntity.class);

        registrar.addBlockData(FurnaceDataProvider.INSTANCE, AbstractFurnaceBlockEntity.class);
        registrar.addBlockData(EnderChestDataProvider.INSTANCE, EnderChestBlockEntity.class);

        registrar.addBlockData(RandomizableContainerDataProvider.INSTANCE, RandomizableContainerBlockEntity.class, 1100);
        registrar.addBlockData(BaseContainerDataProvider.INSTANCE, BaseContainerBlockEntity.class, 1200);
        registrar.addBlockData(HopperContainerDataProvider.INSTANCE, BlockEntity.class, 1300);

        registrar.addEntityData(ContainerEntityDataProvider.INSTANCE, Container.class, 1100);

        registrar.addBlacklist(1100,
            Blocks.BARRIER,
            Blocks.STRUCTURE_VOID);

        registrar.addBlacklist(1100,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.EXPERIENCE_ORB,
            EntityType.FIREBALL,
            EntityType.FIREWORK_ROCKET,
            EntityType.INTERACTION,
            EntityType.SNOWBALL);

        Options.ALIASES.forEach(registrar::addConfigAlias);
    }

}
