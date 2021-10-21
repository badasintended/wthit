package mcp.mobius.waila.plugin.vanilla.config;

import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation FURNACE_CONTENTS       = new ResourceLocation("furnace_contents");
    public static final ResourceLocation ITEM_ENTITY            = new ResourceLocation("item_entity");
    public static final ResourceLocation OVERRIDE_INFESTED      = new ResourceLocation("override.infested");
    public static final ResourceLocation OVERRIDE_TRAPPED_CHEST = new ResourceLocation("override.trapped_chest");
    public static final ResourceLocation OVERRIDE_POWDER_SNOW   = new ResourceLocation("override.powder_snow");
    public static final ResourceLocation PET_OWNER              = new ResourceLocation("pet.owner");
    public static final ResourceLocation PET_HIDE_UNKNOWN_OWNER = new ResourceLocation("pet.hide_unknown_owner");
    public static final ResourceLocation SPAWNER_TYPE           = new ResourceLocation("spawner_type");
    public static final ResourceLocation CROP_PROGRESS          = new ResourceLocation("crop_progress");
    public static final ResourceLocation REDSTONE_LEVER         = new ResourceLocation("redstone.lever");
    public static final ResourceLocation REDSTONE_REPEATER      = new ResourceLocation("redstone.repeater");
    public static final ResourceLocation REDSTONE_COMPARATOR    = new ResourceLocation("redstone.comparator");
    public static final ResourceLocation REDSTONE_LEVEL         = new ResourceLocation("redstone.level");
    public static final ResourceLocation JUKEBOX_RECORD         = new ResourceLocation("jukebox.record");
    public static final ResourceLocation PLAYER_HEAD_NAME       = new ResourceLocation("player_head.name");
    public static final ResourceLocation LEVEL_COMPOSTER        = new ResourceLocation("level.composter");
    public static final ResourceLocation LEVEL_HONEY            = new ResourceLocation("level.honey");
    public static final ResourceLocation NOTE_BLOCK_TYPE        = new ResourceLocation("note_block.type");
    public static final ResourceLocation NOTE_BLOCK_NOTE        = new ResourceLocation("note_block.note");
    public static final ResourceLocation NOTE_BLOCK_INT_VALUE   = new ResourceLocation("note_block.int_value");
    // @formatter:on

    public enum NoteDisplayMode {
        SHARP, FLAT
    }

}
