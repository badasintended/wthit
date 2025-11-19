package mcp.mobius.waila.plugin.vanilla.provider.data;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ListBackedContainer;

public enum SelectableSlotContainerDataProvider implements IDataProvider<BlockEntity> {

    SHELF(Options.SHELF_ITEMS),
    BOOKSHELF(Options.BOOK_BOOKSHELF);

    public static final IData.Type<Data> DATA = IData.createType(ResourceLocation.withDefaultNamespace("chiseled_bookshelf"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_STREAM_CODEC), Data::items,
        Data::new);

    private final ResourceLocation option;

    SelectableSlotContainerDataProvider(ResourceLocation option) {
        this.option = option;
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        var target = (ListBackedContainer) accessor.getTarget();
        data.blockAll(ItemData.TYPE);

        if (config.getBoolean(option)) data.add(DATA, res ->
            res.add(new Data(target.getItems())));
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
