package mcp.mobius.waila.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class PacketIo<I, O> {

    public static final PacketIo<NbtCompound, NbtCompound> ReceiveData = new PacketIo<NbtCompound, NbtCompound>() {
        @Override
        public void write(PacketByteBuf buf, NbtCompound tag) {
            buf.writeNbt(tag);
        }

        @Override
        protected NbtCompound apply(PacketByteBuf buf) {
            return buf.readNbt();
        }
    };

    public static final PacketIo<Entity, Integer> RequestEntity = new PacketIo<Entity, Integer>() {
        @Override
        public void write(PacketByteBuf buf, Entity entity) {
            buf.writeInt(entity.getId());
        }

        @Override
        protected Integer apply(PacketByteBuf buf) {
            return buf.readInt();
        }
    };

    public static final PacketIo<BlockEntity, BlockPos> RequestBlock = new PacketIo<BlockEntity, BlockPos>() {
        @Override
        public void write(PacketByteBuf buf, BlockEntity blockEntity) {
            buf.writeBlockPos(blockEntity.getPos());
        }

        @Override
        protected BlockPos apply(PacketByteBuf buf) {
            return buf.readBlockPos();
        }
    };

    public static final PacketIo<PluginConfig, Map<Identifier, Boolean>> SendConfig = new PacketIo<PluginConfig, Map<Identifier, Boolean>>() {
        @Override
        public void write(PacketByteBuf buf, PluginConfig config) {
            Set<ConfigEntry> entries = config.getSyncableConfigs();
            buf.writeInt(entries.size());
            entries.forEach(e -> {
                buf.writeInt(e.getId().toString().length());
                buf.writeString(e.getId().toString());
                buf.writeBoolean(e.getValue());
            });
        }

        @Override
        protected Map<Identifier, Boolean> apply(PacketByteBuf buf) {
            int size = buf.readInt();
            Map<Identifier, Boolean> map = new HashMap<>();
            for (int j = 0; j < size; j++) {
                int idLength = buf.readInt();
                Identifier id = new Identifier(buf.readString(idLength));
                boolean value = buf.readBoolean();
                map.put(id, value);
            }
            return map;
        }
    };

    public abstract void write(PacketByteBuf buf, I i);

    public PacketByteBuf create(I i) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        write(buf, i);
        return buf;
    }

    protected abstract O apply(PacketByteBuf buf);

    public <R> R apply(PacketByteBuf buf, Function<O, R> fun) {
        return fun.apply(apply(buf));
    }

    public void consume(PacketByteBuf buf, Consumer<O> fun) {
        fun.accept(apply(buf));
    }

}
