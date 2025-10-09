package mcp.mobius.waila.plugin.vanilla;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;
import mcp.mobius.waila.api.IntFormat;
import mcp.mobius.waila.plugin.vanilla.config.EnchantmentDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.NoteDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
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
import net.minecraft.world.RandomizableContainer;
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

public class VanillaCommonPlugin implements IWailaCommonPlugin {

    @Override
    public void register(ICommonRegistrar registrar) {
        registrar.localConfig(Options.BLOCK_POSITION, false);
        registrar.localConfig(Options.BLOCK_STATE, false);

        registrar.featureConfig(Options.ENTITY_ITEM_ENTITY, true);

        registrar.localConfig(Options.ENTITY_POSITION, false);
        registrar.featureConfig(Options.ENTITY_HEALTH, true);
        registrar.featureConfig(Options.ENTITY_ABSORPTION, false);
        registrar.featureConfig(Options.ENTITY_ARMOR, true);
        registrar.localConfig(Options.ENTITY_COMPACT, false);
        registrar.localConfig(Options.ENTITY_ICON_PER_LINE, 25);
        registrar.localConfig(Options.ENTITY_LONG_HEALTH_MAX, 100);
        registrar.localConfig(Options.ENTITY_LONG_ARMOR_MAX, 100);
        registrar.entityData(EntityAttributesDataProvider.INSTANCE, Entity.class);

        registrar.featureConfig(Options.PET_OWNER, false);
        registrar.localConfig(Options.PET_HIDE_UNKNOWN_OWNER, false);
        registrar.entityData(PetOwnerDataProvider.INSTANCE, OwnableEntity.class);

        registrar.featureConfig(Options.HORSE_JUMP_HEIGHT, true);
        registrar.featureConfig(Options.HORSE_SPEED, true);

        registrar.featureConfig(Options.PANDA_GENES, true);

        registrar.featureConfig(Options.BEE_HIVE_POS, false);
        registrar.featureConfig(Options.BEE_HIVE_HONEY_LEVEL, true);
        registrar.featureConfig(Options.BEE_HIVE_OCCUPANTS, false);
        registrar.dataType(BeehiveDataProvider.OCCUPANTS, BeehiveDataProvider.OCCUPANTS_CODEC);
        registrar.dataType(BeeDataProvider.HIVE_POS, BeeDataProvider.HIVE_POS_CODEC);
        registrar.blockData(BeehiveDataProvider.INSTANCE, BeehiveBlockEntity.class);
        registrar.entityData(BeeDataProvider.INSTANCE, Bee.class);

        registrar.featureConfig(Options.EFFECT_BEACON, false);
        registrar.dataType(BeaconDataProvider.DATA, BeaconDataProvider.DATA_CODEC);
        registrar.blockData(BeaconDataProvider.INSTANCE, BeaconBlockEntity.class);

        registrar.featureConfig(Options.EFFECT_MOB, false);
        registrar.syncedConfig(Options.EFFECT_HIDDEN_MOB, false, false);
        registrar.dataType(MobEffectDataProvider.DATA, MobEffectDataProvider.DATA_CODEC);
        registrar.entityData(MobEffectDataProvider.INSTANCE, LivingEntity.class);

        registrar.featureConfig(Options.JUKEBOX_RECORD, false);
        registrar.dataType(JukeboxDataProvider.DATA, JukeboxDataProvider.DATA_CODEC);
        registrar.blockData(JukeboxDataProvider.INSTANCE, JukeboxBlockEntity.class);

        registrar.featureConfig(Options.TIMER_GROW, false);
        registrar.featureConfig(Options.TIMER_BREED, false);
        registrar.entityData(MobTimerDataProvider.INSTANCE, AgeableMob.class);

        registrar.featureConfig(Options.OVERRIDE_INVISIBLE_ENTITY, true);
        registrar.featureConfig(Options.OVERRIDE_TRAPPED_CHEST, true);
        registrar.featureConfig(Options.OVERRIDE_POWDER_SNOW, true);
        registrar.featureConfig(Options.OVERRIDE_INFESTED, true);
        registrar.localConfig(Options.OVERRIDE_VEHICLE, true);

        registrar.featureConfig(Options.BREAKING_PROGRESS, true);
        registrar.localConfig(Options.BREAKING_PROGRESS_COLOR, 0xAAFFFFFF, IntFormat.ARGB_HEX);
        registrar.localConfig(Options.BREAKING_PROGRESS_BOTTOM_ONLY, false);

        registrar.featureConfig(Options.SPAWNER_TYPE, true);

        registrar.featureConfig(Options.PLANT_CROP_PROGRESS, true);
        registrar.featureConfig(Options.PLANT_CROP_GROWABLE, true);
        registrar.featureConfig(Options.PLANT_TREE_GROWABLE, true);

        registrar.featureConfig(Options.REDSTONE_LEVER, true);
        registrar.featureConfig(Options.REDSTONE_REPEATER, true);
        registrar.featureConfig(Options.REDSTONE_COMPARATOR, true);
        registrar.featureConfig(Options.REDSTONE_LEVEL, true);

        registrar.featureConfig(Options.LEVEL_COMPOSTER, true);

        registrar.featureConfig(Options.NOTE_BLOCK_TYPE, true);
        registrar.localConfig(Options.NOTE_BLOCK_NOTE, NoteDisplayMode.SHARP);
        registrar.localConfig(Options.NOTE_BLOCK_INT_VALUE, false);

        registrar.featureConfig(Options.BOOK_BOOKSHELF, false);
        registrar.localConfig(Options.BOOK_ENCHANTMENT_DISPLAY_MODE, EnchantmentDisplayMode.CYCLE);
        registrar.localConfig(Options.BOOK_ENCHANTMENT_CYCLE_TIMING, 500);
        registrar.localConfig(Options.BOOK_WRITTEN, true);
        registrar.dataType(ChiseledBookShelfDataProvider.DATA, ChiseledBookShelfDataProvider.DATA_CODEC);
        registrar.blockData(ChiseledBookShelfDataProvider.INSTANCE, ChiseledBookShelfBlockEntity.class);

        registrar.blockData(FurnaceDataProvider.INSTANCE, AbstractFurnaceBlockEntity.class);
        registrar.blockData(EnderChestDataProvider.INSTANCE, EnderChestBlockEntity.class);

        registrar.blockData(RandomizableContainerDataProvider.INSTANCE, RandomizableContainer.class, 1100);
        registrar.blockData(BaseContainerDataProvider.INSTANCE, BaseContainerBlockEntity.class, 1200);
        registrar.blockData(HopperContainerDataProvider.INSTANCE, BlockEntity.class, 1300);

        registrar.entityData(ContainerEntityDataProvider.INSTANCE, Container.class, 1100);

        registrar.blacklist(1100,
            Blocks.BARRIER,
            Blocks.STRUCTURE_VOID,
            Blocks.LIGHT);

        registrar.blacklist(1100,
            EntityType.AREA_EFFECT_CLOUD,
            EntityType.EXPERIENCE_ORB,
            EntityType.FIREBALL,
            EntityType.FIREWORK_ROCKET,
            EntityType.INTERACTION,
            EntityType.SNOWBALL);

        Options.ALIASES.forEach(registrar::configAlias);
    }

}
