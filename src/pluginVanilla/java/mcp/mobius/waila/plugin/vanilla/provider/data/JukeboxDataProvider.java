package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxDataProvider implements IDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) {
            var stack = accessor.getTarget().getTheItem();
            if (!stack.isEmpty()) {
                var text = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null
                    ? Component.translatable(stack.getDescriptionId() + ".desc")
                    : stack.getDisplayName();
                data.raw().putString("record", Component.Serializer.toJson(text, accessor.getWorld().registryAccess()));
            }
        }
    }

}
