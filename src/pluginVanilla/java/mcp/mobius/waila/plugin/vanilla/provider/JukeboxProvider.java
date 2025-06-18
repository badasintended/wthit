package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.JukeboxDataProvider;

public enum JukeboxProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.JUKEBOX_RECORD)) return;

        var data = accessor.getData().get(JukeboxDataProvider.DATA);
        if (data == null) return;

        tooltip.setLine(Options.JUKEBOX_RECORD, data.record());
    }

}
