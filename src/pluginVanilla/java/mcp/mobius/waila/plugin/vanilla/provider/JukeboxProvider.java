package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;

public enum JukeboxProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD) && accessor.getData().raw().contains("record")) {
            var component = Component.Serializer.fromJson(accessor.getData().raw().getString("record"), accessor.getWorld().registryAccess());
            if (component != null) tooltip.addLine(component);
        }
    }

}
