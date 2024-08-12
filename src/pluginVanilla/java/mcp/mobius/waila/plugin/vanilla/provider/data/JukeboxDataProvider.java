package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxDataProvider implements IDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) {
            var stack = accessor.getTarget().getRecord();
            if (!stack.isEmpty()) {
                var text = stack.getItem() instanceof RecordItem
                    ? Component.translatable(stack.getDescriptionId() + ".desc")
                    : stack.getDisplayName();
                data.raw().putString("record", Component.Serializer.toJson(text));
            }
        }
    }

}
