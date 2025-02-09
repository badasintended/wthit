package mcp.mobius.waila.registry;

import mcp.mobius.waila.api.IPluginInfo;

@SuppressWarnings("deprecation")
public record PluginAware<T>(IPluginInfo plugin, T instance) {
}
