package mcp.mobius.waila.plugin.vanilla.provider.data;

import java.util.List;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

public enum ChiseledBookShelfDataProvider implements IDataProvider<ChiseledBookShelfBlockEntity> {

    INSTANCE;

    public static final ResourceLocation DATA = new ResourceLocation("chiseled_bookshelf");

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChiseledBookShelfBlockEntity> accessor, IPluginConfig config) {
        data.blockAll(ItemData.class);

        if (config.getBoolean(Options.BOOK_BOOKSHELF)) data.add(Data.class, res -> {
            var bookshelf = (ChiseledBookShelfBlockEntityAccess) accessor.getTarget();
            res.add(new Data(bookshelf.wthit_items()));
        });
    }

    public record Data(
        List<ItemStack> items
    ) implements IData {

        public Data(FriendlyByteBuf buf) {
            this(buf.readList(FriendlyByteBuf::readItem));
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeCollection(items, FriendlyByteBuf::writeItem);
        }

    }

}
