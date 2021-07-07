package mcp.mobius.waila.api;

import mcp.mobius.waila.Waila;
import net.minecraft.util.Identifier;

public class WailaConstants {

    // @formatter:off
    public static final String WAILA    = "waila";
    public static final String WTHIT    = "wthit";
    public static final String MOD_ID   = WTHIT;
    public static final String MOD_NAME = "WTHIT";

    // Used for validating config.
    public static final int CONFIG_VERSION = 1;

    // Common tooltip tags, to be used for overriding core tooltip values.
    // To do that, cast List<Text> to ITaggableList<Identifier, Text> and call setTag().
    public static final Identifier OBJECT_NAME_TAG   = Waila.id("object_name");   // Block, Fluid, or Entity name
    public static final Identifier REGISTRY_NAME_TAG = Waila.id("registry_name");
    public static final Identifier MOD_NAME_TAG      = Waila.id("mod_name");

    // PluginConfig keys from core plugin
    public static final Identifier CONFIG_SHOW_BLOCK    = Waila.id("show_blocks");
    public static final Identifier CONFIG_SHOW_FLUID    = Waila.id("show_fluids");
    public static final Identifier CONFIG_SHOW_ENTITY   = Waila.id("show_entities");
    public static final Identifier CONFIG_SHOW_ITEM     = Waila.id("show_item");
    public static final Identifier CONFIG_SHOW_MOD_NAME = Waila.id("show_mod_name");
    public static final Identifier CONFIG_SHOW_REGISTRY = Waila.id("show_registry");
    // @formatter:on

}
