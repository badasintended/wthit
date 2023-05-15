package mcp.mobius.waila.plugin.extra.config;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.ResourceLocation;

public final class Options {

    // @formatter:off
    public static final ResourceLocation ENERGY = rl("energy.enabled");
    // @formatter:on

    private static ResourceLocation rl(String rl) {
        return new ResourceLocation(WailaConstants.NAMESPACE + "x", rl);
    }

}
