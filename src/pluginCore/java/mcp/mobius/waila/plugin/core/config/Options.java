package mcp.mobius.waila.plugin.core.config;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation STATES = id("show_states");
    public static final ResourceLocation POS    = id("show_pos");
    // @formatter:on

    private static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE, path);
    }

}
