package mcp.mobius.waila.plugin.harvest.config;

import net.minecraft.resources.ResourceLocation;

public class Options {

    // @formatter:off
    public static final ResourceLocation ENABLED      = rl("enabled");
    public static final ResourceLocation DISPLAY_MODE = rl("display_mode");
    // @formatter:on

    public static ResourceLocation rl(String path) {
        return new ResourceLocation("harvest", path);
    }

}
