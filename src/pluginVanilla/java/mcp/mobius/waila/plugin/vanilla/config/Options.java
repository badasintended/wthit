package mcp.mobius.waila.plugin.vanilla.config;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation BREAKING_PROGRESS             = rl("breaking_progress.enabled");
    public static final ResourceLocation BREAKING_PROGRESS_COLOR       = rl("breaking_progress.color");
    public static final ResourceLocation BREAKING_PROGRESS_BOTTOM_ONLY = rl("breaking_progress.bottom_only");
    public static final ResourceLocation OVERRIDE_INFESTED             = rl("override.infested");
    public static final ResourceLocation OVERRIDE_TRAPPED_CHEST        = rl("override.trapped_chest");
    public static final ResourceLocation OVERRIDE_POWDER_SNOW          = rl("override.powder_snow");
    public static final ResourceLocation OVERRIDE_VEHICLE              = rl("override.vehicle");
    public static final ResourceLocation OVERRIDE_INVISIBLE_ENTITY     = rl("override.invisible_entity");
    public static final ResourceLocation PET_OWNER                     = rl("pet.owner");
    public static final ResourceLocation PET_HIDE_UNKNOWN_OWNER        = rl("pet.hide_unknown_owner");
    public static final ResourceLocation SPAWNER_TYPE                  = rl("spawner.type");
    public static final ResourceLocation PLANT_CROP_PROGRESS           = rl("plant.crop_progress");
    public static final ResourceLocation PLANT_CROP_GROWABLE           = rl("plant.crop_growable");
    public static final ResourceLocation PLANT_TREE_GROWABLE           = rl("plant.tree_growable");
    public static final ResourceLocation REDSTONE_LEVER                = rl("redstone.lever");
    public static final ResourceLocation REDSTONE_REPEATER             = rl("redstone.repeater");
    public static final ResourceLocation REDSTONE_COMPARATOR           = rl("redstone.comparator");
    public static final ResourceLocation REDSTONE_LEVEL                = rl("redstone.level");
    public static final ResourceLocation JUKEBOX_RECORD                = rl("jukebox.record");
    public static final ResourceLocation LEVEL_COMPOSTER               = rl("level.composter");
    public static final ResourceLocation NOTE_BLOCK_TYPE               = rl("note_block.type");
    public static final ResourceLocation NOTE_BLOCK_NOTE               = rl("note_block.note");
    public static final ResourceLocation NOTE_BLOCK_INT_VALUE          = rl("note_block.int_value");
    public static final ResourceLocation TIMER_GROW                    = rl("timer.grow");
    public static final ResourceLocation TIMER_BREED                   = rl("timer.breed");
    public static final ResourceLocation BLOCK_POSITION                = rl("block.position");
    public static final ResourceLocation BLOCK_STATE                   = rl("block.state");
    public static final ResourceLocation ENTITY_ITEM_ENTITY            = rl("entity.item_entity");
    public static final ResourceLocation ENTITY_POSITION               = rl("entity.position");
    public static final ResourceLocation ENTITY_HEALTH                 = rl("entity.health");
    public static final ResourceLocation ENTITY_ABSORPTION             = rl("entity.absorption");
    public static final ResourceLocation ENTITY_ARMOR                  = rl("entity.armor");
    public static final ResourceLocation ENTITY_COMPACT                = rl("entity.compact");
    public static final ResourceLocation ENTITY_ICON_PER_LINE          = rl("entity.icon_per_line");
    public static final ResourceLocation ENTITY_LONG_HEALTH_MAX        = rl("entity.long_health_max");
    public static final ResourceLocation ENTITY_LONG_ARMOR_MAX         = rl("entity.long_armor_max");
    public static final ResourceLocation BEE_HIVE_POS                  = rl("bee.hive_pos");
    public static final ResourceLocation BEE_HIVE_HONEY_LEVEL          = rl("bee.hive_honey_level");
    public static final ResourceLocation BEE_HIVE_OCCUPANTS            = rl("bee.hive_occupants");
    public static final ResourceLocation HORSE_JUMP_HEIGHT             = rl("horse.jump_height");
    public static final ResourceLocation HORSE_SPEED                   = rl("horse.speed");
    public static final ResourceLocation PANDA_GENES                   = rl("panda.genes");
    public static final ResourceLocation EFFECT_BEACON                 = rl("effect.beacon");
    public static final ResourceLocation EFFECT_MOB                    = rl("effect.mob");
    public static final ResourceLocation EFFECT_HIDDEN_MOB             = rl("effect.hidden_mob");
    public static final ResourceLocation BOOK_BOOKSHELF                = rl("book.bookshelf");
    public static final ResourceLocation BOOK_WRITTEN                  = rl("book.written");
    public static final ResourceLocation BOOK_ENCHANTMENT_DISPLAY_MODE = rl("book.enchantment");
    public static final ResourceLocation BOOK_ENCHANTMENT_CYCLE_TIMING = rl("book.enchantment_cycle_timing");
    // @formatter:on

    // @formatter:off
    public static final Map<ResourceLocation, ResourceLocation> ALIASES = new ImmutableMap.Builder<ResourceLocation, ResourceLocation>()
        .put(SPAWNER_TYPE          , rl("spawner_type"))
        .put(PLANT_CROP_PROGRESS   , rl("crop_progress"))
        .put(PLANT_CROP_GROWABLE   , rl("crop_growable"))
        .put(PLANT_TREE_GROWABLE   , rl("tree_growable"))
        .put(BLOCK_POSITION        , rl("attribute.block_position"))
        .put(BLOCK_STATE           , rl("attribute.block_state"))
        .put(ENTITY_ITEM_ENTITY    , rl("item_entity"))
        .put(ENTITY_POSITION       , rl("attribute.entity_position"))
        .put(ENTITY_HEALTH         , rl("attribute.health"))
        .put(ENTITY_ABSORPTION     , rl("attribute.absorption"))
        .put(ENTITY_ARMOR          , rl("attribute.armor"))
        .put(ENTITY_COMPACT        , rl("attribute.compact"))
        .put(ENTITY_ICON_PER_LINE  , rl("attribute.icon_per_line"))
        .put(ENTITY_LONG_HEALTH_MAX, rl("attribute.long_health_max"))
        .put(ENTITY_LONG_ARMOR_MAX , rl("attribute.long_armor_max"))
        .put(BEE_HIVE_HONEY_LEVEL  , rl("level.honey"))
        .put(HORSE_JUMP_HEIGHT     , rl("attribute.horse_jump_height"))
        .put(HORSE_SPEED           , rl("attribute.horse_speed"))
        .put(PANDA_GENES           , rl("attribute.panda_genes"))
        .put(EFFECT_BEACON         , rl("attribute.beacon_effects"))
        .put(EFFECT_MOB            , rl("attribute.mob_effects"))
        .put(EFFECT_HIDDEN_MOB     , rl("attribute.hidden_mob_effects"))
        .build();
    // @formatter:on

    private static ResourceLocation rl(String rl) {
        return ResourceLocation.withDefaultNamespace(rl);
    }

}
