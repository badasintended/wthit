package mcp.mobius.waila.config;

import mcp.mobius.waila.api.IJsonConfig;

@IJsonConfig.Comment("Debug options, restart the game to apply")
public class DebugConfig {

    @IJsonConfig.Comment("Show test plugin on plugin toggle screen")
    public boolean showTestPluginToggle = false;

}
