package mcp.mobius.waila.plugin.vanilla.provider.data;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

public enum ChiseledBookShelfDataProvider implements IDataProvider<ChiseledBookShelfBlockEntity> {

    INSTANCE;

    public static final IData.Type<Data> DATA = IData.createType(ResourceLocation.withDefaultNamespace("chiseled_bookshelf"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_STREAM_CODEC), Data::items,
        Data::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChiseledBookShelfBlockEntity> accessor, IPluginConfig config) {
        data.blockAll(ItemData.TYPE);

        if (config.getBoolean(Options.BOOK_BOOKSHELF)) data.add(DATA, res -> {
            var bookshelf = (ChiseledBookShelfBlockEntityAccess) accessor.getTarget();
            res.add(new Data(bookshelf.wthit_items()));
        });
    }

    public record Data(
        List<ItemStack> items
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }

}
