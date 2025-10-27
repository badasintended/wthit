package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxDataProvider implements IDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.JUKEBOX_RECORD)) return;

        var song = accessor.getTarget().getSongPlayer().getSong();
        if (song != null) {
            data.raw().putString("record", Component.Serializer.toJson(song.description(), accessor.getWorld().registryAccess()));
            data.blockAll(ItemData.TYPE);
        }
    }

}
