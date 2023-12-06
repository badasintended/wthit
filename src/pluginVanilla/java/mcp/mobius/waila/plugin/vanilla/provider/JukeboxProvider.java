package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxProvider implements IBlockComponentProvider, IDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD) && accessor.getData().raw().contains("record")) {
            tooltip.addLine(Component.Serializer.fromJson(accessor.getData().raw().getString("record")));
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) {
            var stack = accessor.getTarget().getTheItem();
            if (!stack.isEmpty()) {
                var text = stack.getItem() instanceof RecordItem
                    ? Component.translatable(stack.getDescriptionId() + ".desc")
                    : stack.getDisplayName();
                data.raw().putString("record", Component.Serializer.toJson(text));
            }
        }
    }

}
