package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.mixin.LecternBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public enum LecternDataProvider implements IDataProvider<LecternBlockEntity> {

    INSTANCE;

    public static final ResourceLocation DATA = new ResourceLocation("lectern");

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

        public Data(FriendlyByteBuf buf) {
            this(buf.readItem(), buf.readVarInt(), buf.readVarInt());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeItem(book);
            buf.writeVarInt(page);
            buf.writeVarInt(pageCount);
        }

    }

}
