package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxDataProvider implements IDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    public static final IData.Type<Data> DATA = IData.createType(ResourceLocation.withDefaultNamespace("jukebox"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC, Data::record,
        Data::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) data.add(DATA, res -> {
            var stack = accessor.getTarget().getTheItem();
            if (!stack.isEmpty()) {
                var playable = stack.get(DataComponents.JUKEBOX_PLAYABLE);
                Component text = null;

                if (playable != null) {
                    var song = playable.song().unwrap(accessor.getPlayer().registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG));
                    if (song.isPresent()) text = song.get().description();
                }

                if (text == null) text = stack.getDisplayName();
                res.add(new Data(text));
            }
        });
    }

    public record Data(
        Component record
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }

}
