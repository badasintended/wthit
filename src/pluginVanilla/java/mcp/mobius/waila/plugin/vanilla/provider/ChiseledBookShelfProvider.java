package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockAccess;
import mcp.mobius.waila.mixin.ChiseledBookShelfBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum ChiseledBookShelfProvider implements IBlockComponentProvider, IDataProvider<ChiseledBookShelfBlockEntity> {

    INSTANCE;

    public static final IData.Type<Data> DATA = IData.createType(new ResourceLocation("chiseled_bookshelf"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), Data::items,
        Data::new);

    private int lastUpdateId = 0;
    private ItemStack hitItem = ItemStack.EMPTY;

    private void init(IBlockAccessor accessor, IPluginConfig config) {
        if (lastUpdateId == accessor.getUpdateId()) return;

        lastUpdateId = accessor.getUpdateId();
        hitItem = ItemStack.EMPTY;
        if (!config.getBoolean(Options.BOOK_BOOKSHELF)) return;

        var data = accessor.getData().get(DATA);
        if (data == null) return;

        var blockstate = accessor.getBlockState();
        var facing = blockstate.getValue(HorizontalDirectionalBlock.FACING);
        var relativeHit = ChiseledBookShelfBlockAccess.wthit_getRelativeHitCoordinatesForBlockFace(accessor.getBlockHitResult(), facing);
        if (relativeHit.isEmpty()) return;

        var hitSlot = ChiseledBookShelfBlockAccess.wthit_getHitSlot(relativeHit.get());
        hitItem = data.items.get(hitSlot);
    }

    @Override
    public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return null;

        return new ItemComponent(hitItem);
    }

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;

        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(hitItem.getHoverName().getString()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(hitItem.getItem())));
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;

        ItemEntityProvider.appendBookProperties(tooltip, hitItem, config);
    }

    @Override
    public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        init(accessor, config);
        if (hitItem.isEmpty()) return;
        if (!config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) return;

        tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(hitItem).getName()));
    }

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
