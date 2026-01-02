package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.mixin.LecternBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public enum LecternDataProvider implements IDataProvider<LecternBlockEntity> {

    INSTANCE;

    public static final IData.Type<Data> DATA = IData.createType(Identifier.withDefaultNamespace("lectern"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC, Data::book,
        ByteBufCodecs.VAR_INT, Data::page,
        ByteBufCodecs.VAR_INT, Data::pageCount,
        Data::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<LecternBlockEntity> accessor, IPluginConfig config) {
        if (!config.getBoolean(Options.BOOK_LECTERN)) return;

        var lectern = (LecternBlockEntity & LecternBlockEntityAccess) accessor.getTarget();
        if (!lectern.hasBook()) return;
        data.addImmediate(new Data(lectern.getBook(), lectern.getPage() + 1, lectern.wthit_pageCount()));
    }

    public record Data(
        ItemStack book,
        int page, int pageCount
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }

}
