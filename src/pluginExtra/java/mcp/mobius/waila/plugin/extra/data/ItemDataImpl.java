package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemDataImpl extends ItemData {

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDataImpl> CODEC = StreamCodec.ofMember((d, buf) -> {
        var syncNbt = d.config.getBoolean(CONFIG_SYNC_NBT);
        buf.writeBoolean(syncNbt);
        buf.writeVarInt(d.items.size());

        for (var stack : d.items) {
            if (stack.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                ITEM_CODEC.encode(buf, stack.getItemHolder());
                buf.writeVarInt(stack.getCount());
                if (syncNbt) DataComponentPatch.STREAM_CODEC.encode(buf, stack.getComponentsPatch());
            }
        }
    }, buf -> {
        //noinspection DataFlowIssue
        var d = new ItemDataImpl(null);

        d.syncNbt = buf.readBoolean();
        var size = buf.readVarInt();
        d.ensureSpace(size);

        for (var i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            var item = ITEM_CODEC.decode(buf);
            var count = buf.readVarInt();
            var data = d.syncNbt ? DataComponentPatch.STREAM_CODEC.decode(buf) : DataComponentPatch.EMPTY;

            var stack = new ItemStack(item, count, data);
            d.add(stack);
        }

        return d;
    });

    private final IPluginConfig config;
    private boolean syncNbt;

    public ItemDataImpl(IPluginConfig config) {
        this.config = config;
    }

    @Override
    public Type<? extends IData> type() {
        return TYPE;
    }

    public ArrayList<ItemStack> items() {
        return items;
    }

    public boolean syncNbt() {
        return syncNbt;
    }

}
