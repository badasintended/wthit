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

    public static final PacketIo<Void, Void> GenerateClientDump = new PacketIo<>() {
        @Override
        public void write(FriendlyByteBuf buf, Void unused) {
        }

        @Override
        protected Void apply(FriendlyByteBuf buf) {
            return null;
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

    public static final PacketIo<PluginConfig, Map<ResourceLocation, Object>> SendConfig = new PacketIo<>() {
        static final int BOOL = 0;
        static final int INT = 1;
        static final int DOUBLE = 2;
        static final int STRING = 3;

        @Override
        public void write(FriendlyByteBuf buf, PluginConfig config) {
            Set<ConfigEntry<Object>> entries = config.getSyncableConfigs();
            buf.writeVarInt(entries.size());
            for (ConfigEntry<Object> e : entries) {
                buf.writeResourceLocation(e.getId());

                Object v = e.getValue();
                if (v instanceof Boolean z) {
                    buf.writeVarInt(BOOL);
                    buf.writeBoolean(z);
                } else if (v instanceof Integer i) {
                    buf.writeVarInt(INT);
                    buf.writeVarInt(i);
                } else if (v instanceof Double d) {
                    buf.writeVarInt(DOUBLE);
                    buf.writeDouble(d);
                } else if (v instanceof String str) {
                    buf.writeVarInt(STRING);
                    buf.writeUtf(str);
                } else if (v instanceof Enum<?> en) {
                    buf.writeVarInt(STRING);
                    buf.writeUtf(en.name());
                }
            }
        }

        @Override
        protected Map<ResourceLocation, Object> apply(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<ResourceLocation, Object> map = new HashMap<>();
            for (int j = 0; j < size; j++) {
                ResourceLocation id = buf.readResourceLocation();
                int type = buf.readVarInt();
                switch (type) {
                    case BOOL -> map.put(id, buf.readBoolean());
                    case INT -> map.put(id, buf.readVarInt());
                    case DOUBLE -> map.put(id, buf.readDouble());
                    case STRING -> map.put(id, buf.readUtf());
                }
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
