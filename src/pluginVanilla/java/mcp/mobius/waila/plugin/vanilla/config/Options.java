package mcp.mobius.waila.plugin.vanilla.config;

import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation BREAKING_PROGRESS             = rl("breaking_progress.enabled");
    public static final ResourceLocation BREAKING_PROGRESS_COLOR       = rl("breaking_progress.color");
    public static final ResourceLocation BREAKING_PROGRESS_BOTTOM_ONLY = rl("breaking_progress.bottom_only");
    public static final ResourceLocation ITEM_ENTITY                   = rl("item_entity");
    public static final ResourceLocation OVERRIDE_INFESTED             = rl("override.infested");
    public static final ResourceLocation OVERRIDE_TRAPPED_CHEST        = rl("override.trapped_chest");
    public static final ResourceLocation OVERRIDE_POWDER_SNOW          = rl("override.powder_snow");
    public static final ResourceLocation OVERRIDE_VEHICLE              = rl("override.vehicle");
    public static final ResourceLocation OVERRIDE_INVISIBLE_ENTITY     = rl("override.invisible_entity");
    public static final ResourceLocation PET_OWNER                     = rl("pet.owner");
    public static final ResourceLocation PET_HIDE_UNKNOWN_OWNER        = rl("pet.hide_unknown_owner");
    public static final ResourceLocation SPAWNER_TYPE                  = rl("spawner_type");
    public static final ResourceLocation CROP_PROGRESS                 = rl("crop_progress");
    public static final ResourceLocation REDSTONE_LEVER                = rl("redstone.lever");
    public static final ResourceLocation REDSTONE_REPEATER             = rl("redstone.repeater");
    public static final ResourceLocation REDSTONE_COMPARATOR           = rl("redstone.comparator");
    public static final ResourceLocation REDSTONE_LEVEL                = rl("redstone.level");
    public static final ResourceLocation JUKEBOX_RECORD                = rl("jukebox.record");
    public static final ResourceLocation PLAYER_HEAD_NAME              = rl("player_head.name");
    public static final ResourceLocation LEVEL_COMPOSTER               = rl("level.composter");
    public static final ResourceLocation LEVEL_HONEY                   = rl("level.honey");
    public static final ResourceLocation NOTE_BLOCK_TYPE               = rl("note_block.type");
    public static final ResourceLocation NOTE_BLOCK_NOTE               = rl("note_block.note");
    public static final ResourceLocation NOTE_BLOCK_INT_VALUE          = rl("note_block.int_value");
    public static final ResourceLocation TIMER_GROW                    = rl("timer.grow");
    public static final ResourceLocation TIMER_BREED                   = rl("timer.breed");
    public static final ResourceLocation ATTRIBUTE_BLOCK_POSITION      = rl("attribute.block_position");
    public static final ResourceLocation ATTRIBUTE_BLOCK_STATE         = rl("attribute.block_state");
    public static final ResourceLocation ATTRIBUTE_ENTITY_POSITION     = rl("attribute.entity_position");
    public static final ResourceLocation ATTRIBUTE_HEALTH              = rl("attribute.health");
    public static final ResourceLocation ATTRIBUTE_ABSORPTION          = rl("attribute.absorption");
    public static final ResourceLocation ATTRIBUTE_ARMOR               = rl("attribute.armor");
    public static final ResourceLocation ATTRIBUTE_COMPACT             = rl("attribute.compact");
    public static final ResourceLocation ATTRIBUTE_ICON_PER_LINE       = rl("attribute.icon_per_line");
    public static final ResourceLocation ATTRIBUTE_LONG_HEALTH_MAX     = rl("attribute.long_health_max");
    public static final ResourceLocation ATTRIBUTE_LONG_ARMOR_MAX      = rl("attribute.long_armor_max");
    public static final ResourceLocation ATTRIBUTE_HORSE_JUMP_HEIGHT   = rl("attribute.horse_jump_height");
    public static final ResourceLocation ATTRIBUTE_HORSE_SPEED         = rl("attribute.horse_speed");
    public static final ResourceLocation ATTRIBUTE_PANDA_GENES         = rl("attribute.panda_genes");
    public static final ResourceLocation ATTRIBUTE_BEACON_EFFECTS      = rl("attribute.beacon_effects");
    public static final ResourceLocation BOOK_WRITTEN                  = rl("book.written");
    public static final ResourceLocation BOOK_ENCHANTMENT_DISPLAY_MODE = rl("book.enchantment");
    public static final ResourceLocation BOOK_ENCHANTMENT_CYCLE_TIMING = rl("book.enchantment_cycle_timing");
    // @formatter:on

    private static ResourceLocation rl(String rl) {
        return new ResourceLocation(rl);
    }

}
