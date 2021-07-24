package mcp.mobius.waila.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class PacketIo<I, O> {

    public static final PacketIo<CompoundTag, CompoundTag> ReceiveData = new PacketIo<>() {
        @Override
        public void write(FriendlyByteBuf buf, CompoundTag tag) {
            buf.writeNbt(tag);
        }

        @Override
        protected CompoundTag apply(FriendlyByteBuf buf) {
            return buf.readNbt();
        }
    };

    public static final PacketIo<Entity, Integer> RequestEntity = new PacketIo<>() {
        @Override
        public void write(FriendlyByteBuf buf, Entity entity) {
            buf.writeInt(entity.getId());
        }

        @Override
        protected Integer apply(FriendlyByteBuf buf) {
            return buf.readInt();
        }
    };

    public static final PacketIo<BlockEntity, BlockPos> RequestBlock = new PacketIo<>() {
        @Override
        public void write(FriendlyByteBuf buf, BlockEntity blockEntity) {
            buf.writeBlockPos(blockEntity.getBlockPos());
        }

        @Override
        protected BlockPos apply(FriendlyByteBuf buf) {
            return buf.readBlockPos();
        }
    };

    public static final PacketIo<PluginConfig, Map<ResourceLocation, Boolean>> SendConfig = new PacketIo<>() {
        @Override
        public void write(FriendlyByteBuf buf, PluginConfig config) {
            Set<ConfigEntry> entries = config.getSyncableConfigs();
            buf.writeInt(entries.size());
            entries.forEach(e -> {
                buf.writeInt(e.getId().toString().length());
                buf.writeUtf(e.getId().toString());
                buf.writeBoolean(e.getValue());
            });
        }

        @Override
        protected Map<ResourceLocation, Boolean> apply(FriendlyByteBuf buf) {
            int size = buf.readInt();
            Map<ResourceLocation, Boolean> map = new HashMap<>();
            for (int j = 0; j < size; j++) {
                int idLength = buf.readInt();
                ResourceLocation id = new ResourceLocation(buf.readUtf(idLength));
                boolean value = buf.readBoolean();
                map.put(id, value);
            }
            return map;
        }
    };

    public abstract void write(FriendlyByteBuf buf, I i);

    public FriendlyByteBuf create(I i) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        write(buf, i);
        return buf;
    }

    protected abstract O apply(FriendlyByteBuf buf);

    public <R> R apply(FriendlyByteBuf buf, Function<O, R> fun) {
        return fun.apply(apply(buf));
    }

    public void consume(FriendlyByteBuf buf, Consumer<O> fun) {
        fun.accept(apply(buf));
    }

}
