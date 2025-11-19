package mcp.mobius.waila.plugin.harvest.config;

import net.minecraft.resources.Identifier;

public class Options {

    // @formatter:off
    public static final Identifier ENABLED      = rl("enabled");
    public static final Identifier DISPLAY_MODE = rl("display_mode");
    public static final Identifier CREATIVE     = rl("creative");

    public static final Identifier DEV_DISABLE_CACHE = rl("dev.disable_cache");
    // @formatter:on

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath("harvest", path);
    }

}
