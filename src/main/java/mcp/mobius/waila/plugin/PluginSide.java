package mcp.mobius.waila.plugin;

import mcp.mobius.waila.api.IPluginInfo;

public enum PluginSide {
    CLIENT, DEDICATED_SERVER, COMMON;

    public boolean matches(PluginSide other) {
        return this == COMMON || other == COMMON || this == other;
    }

    @SuppressWarnings("deprecation")
    public IPluginInfo.Side toDeprecated() {
        return switch (this) {
            case COMMON -> IPluginInfo.Side.BOTH;
            case CLIENT -> IPluginInfo.Side.CLIENT;
            case DEDICATED_SERVER -> IPluginInfo.Side.SERVER;
        };
    }
}
