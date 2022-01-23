package mcp.mobius.waila.api;

import net.minecraft.resources.ResourceLocation;

public class WailaConstants {

    // @formatter:off
    public static final String WAILA     = "waila";
    public static final String WTHIT     = "wthit";
    public static final String NAMESPACE = WAILA;
    public static final String MOD_ID    = WTHIT;
    public static final String MOD_NAME  = "WTHIT";

    // Used for validating config.
    public static final int CONFIG_VERSION = 1;

    // Common tooltip tags, to be used for overriding core tooltip values.
    // To do that, use ITooltip.setLine(tag, component).
    public static final ResourceLocation OBJECT_NAME_TAG   = id("object_name");   // Block, Fluid, or Entity name
    public static final ResourceLocation REGISTRY_NAME_TAG = id("registry_name");
    public static final ResourceLocation MOD_NAME_TAG      = id("mod_name");

    // PluginConfig keys from core plugin
    public static final ResourceLocation CONFIG_SHOW_BLOCK    = id("show_blocks");
    public static final ResourceLocation CONFIG_SHOW_FLUID    = id("show_fluids");
    public static final ResourceLocation CONFIG_SHOW_ENTITY   = id("show_entities");
    public static final ResourceLocation CONFIG_SHOW_ITEM     = id("show_item");
    public static final ResourceLocation CONFIG_SHOW_MOD_NAME = id("show_mod_name");
    public static final ResourceLocation CONFIG_SHOW_REGISTRY = id("show_registry");
    // @formatter:on

    private static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }

}
