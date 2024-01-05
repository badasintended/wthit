package mcp.mobius.waila.plugin.extra.data;

import java.util.ArrayList;
import java.util.Objects;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class ItemDataImpl extends ItemData {

    private final IPluginConfig config;
    private boolean syncNbt;

    public ItemDataImpl(IPluginConfig config) {
        this.config = config;
    }

    public ItemDataImpl(FriendlyByteBuf buf) {
        this.config = null;

        syncNbt = buf.readBoolean();
        var size = buf.readVarInt();
        ensureSpace(size);

        for (var i = 0; i < size; i++) {
            if (buf.readBoolean()) continue;

            var item = buf.readById(Registry.ITEM);
            var count = buf.readVarInt();
            var stack = new ItemStack(Objects.requireNonNull(item), count);
            if (syncNbt) stack.setTag(buf.readNbt());
            add(stack);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        var syncNbt = config.getBoolean(CONFIG_SYNC_NBT);
        buf.writeBoolean(syncNbt);
        buf.writeVarInt(items.size());

        for (var stack : items) {
            if (stack.isEmpty()) {
                buf.writeBoolean(true);
            } else {
                buf.writeBoolean(false);
                buf.writeId(Registry.ITEM, stack.getItem());
                buf.writeVarInt(stack.getCount());
                if (syncNbt) buf.writeNbt(stack.getTag());
            }
        }
    }

    public ArrayList<ItemStack> items() {
        return items;
    }

    public boolean syncNbt() {
        return syncNbt;
    }

}
