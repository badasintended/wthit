package mcp.mobius.waila.api;

import net.minecraft.resources.ResourceLocation;

public class WailaConstants {

    // @formatter:off
    public static final String WAILA     = "waila";
    public static final String WTHIT     = "wthit";
    public static final String NAMESPACE = WAILA;
    public static final String MOD_ID    = WTHIT;
    public static final String MOD_NAME  = "WTHIT";
    // @formatter:on

    /**
     * Used for validating config.
     */
    public static final int CONFIG_VERSION = 1;

    /**
     * The default priority for all component and data provider.
     */
    public static final int DEFAULT_PRIORITY = 1000;

    /**
     * Tooltip tag for block, fluid, and entity name line.
     *
     * @see ITooltip#setLine
     * @see IWailaConfig.Formatter#blockName(Object)
     * @see IWailaConfig.Formatter#fluidName(Object)
     * @see IWailaConfig.Formatter#entityName(Object)
     */
    public static final ResourceLocation OBJECT_NAME_TAG = id("object_name");

    /**
     * Tooltip tag for registry name line.
     *
     * @see ITooltip#setLine
     * @see #CONFIG_SHOW_REGISTRY
     * @see IWailaConfig.Formatter#registryName(Object)
     */
    public static final ResourceLocation REGISTRY_NAME_TAG = id("registry_name");

    /**
     * Tooltip tag for mod name line.
     *
     * @see ITooltip#setLine
     * @see #CONFIG_SHOW_MOD_NAME
     * @see IWailaConfig.Formatter#modName(Object)
     */
    public static final ResourceLocation MOD_NAME_TAG = id("mod_name");

    /**
     * Tooltip tag for errors.
     *
     * @see ITooltip#setLine
     */
    public static final ResourceLocation ERROR_TAG = id("error");

    /**
     * Whether Waila should show tooltip for blocks.
     * <p>
     * <b>Default value:</b> {@code true}
     */
    public static final ResourceLocation CONFIG_SHOW_BLOCK = id("show_blocks");

    /**
     * Whether Waila should show tooltip for fluids.
     * <p>
     * <b>Default value:</b> {@code false}
     */
    public static final ResourceLocation CONFIG_SHOW_FLUID = id("show_fluids");

    /**
     * Whether Waila should show tooltip for entities.
     * <p>
     * <b>Default value:</b> {@code true}
     */
    public static final ResourceLocation CONFIG_SHOW_ENTITY = id("show_entities");

    /**
     * Whether Waila should show icon at the side of the tooltip.
     * <p>
     * <b>Default value:</b> {@code true}
     */
    public static final ResourceLocation CONFIG_SHOW_ICON = id("show_icon");

    /**
     * Where the tooltip icon should be positioned.
     * <p>
     * <b>Default value:</b> {@link IWailaConfig.Overlay.Position.Align.Y#MIDDLE}
     */
    public static final ResourceLocation CONFIG_ICON_POSITION = id("icon_position");

    /**
     * Whether Waila should show the name of the mod the object originated from.
     * <p>
     * <b>Default value:</b> {@code true}
     */
    public static final ResourceLocation CONFIG_SHOW_MOD_NAME = id("show_mod_name");

    /**
     * Whether Waila should show the name of the mod the item originated from when hovering it in screen.
     * <p>
     * <b>Default value:</b> {@code true}
     */
    public static final ResourceLocation CONFIG_SHOW_ITEM_MOD_NAME = id("show_item_mod_name");

    /**
     * Whether Waila should show the registry name of the object.
     * <p>
     * <b>Default value:</b> {@code false}
     */
    public static final ResourceLocation CONFIG_SHOW_REGISTRY = id("show_registry");

    /**
     * Built-in components texture.
     */
    public static final ResourceLocation COMPONENT_TEXTURE = id("textures/components.png");

    /**
     * Gradient theme id.
     */
    public static final ResourceLocation THEME_TYPE_GRADIENT = id("gradient");

    /**
     * Nine patch theme id.
     */
    public static final ResourceLocation THEME_TYPE_NINE_PATCH = id("nine_patch");

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

}
