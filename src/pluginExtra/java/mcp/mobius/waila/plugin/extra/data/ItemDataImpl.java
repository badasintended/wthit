package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;
import java.util.Objects;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ItemDataImpl extends ItemData {

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDataImpl> CODEC = StreamCodec.ofMember((d, buf) -> {
        var syncNbt = d.config.getBoolean(CONFIG_SYNC_NBT);
        buf.writeBoolean(syncNbt);
        buf.writeVarInt(d.items.size());

        for (var stack : d.items) {
            if (stack.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeById(BuiltInRegistries.ITEM::getId, stack.getItem());
                buf.writeVarInt(stack.getCount());
                if (syncNbt) DataComponentPatch.STREAM_CODEC.encode(buf, stack.getComponentsPatch());
            }
        }
    }, buf -> {
        var d = new ItemDataImpl(null);

        d.syncNbt = buf.readBoolean();
        var size = buf.readVarInt();
        d.ensureSpace(size);

        for (var i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            var item = buf.readById(BuiltInRegistries.ITEM::byId);
            var count = buf.readVarInt();
            var stack = new ItemStack(Objects.requireNonNull(item), count);
            if (d.syncNbt) stack.applyComponents(DataComponentPatch.STREAM_CODEC.decode(buf));
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
