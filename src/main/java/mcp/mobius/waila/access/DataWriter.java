package mcp.mobius.waila.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public enum DataWriter implements IDataWriter {

    SERVER(Packets.DATA_RAW, Packets.DATA_TYPED),
    CLIENT(Packets.CTX_RAW, Packets.CTX_TYPED);

    private final ResourceLocation rawPacket;
    private final ResourceLocation typedPacket;

    private final Map<Class<IData>, IData> immediate = new HashMap<>();
    private final Map<Class<IData>, List<Consumer<Result<IData>>>> lazy = new HashMap<>();

    private CompoundTag raw;
    private boolean clean;

    DataWriter(ResourceLocation rawPacket, ResourceLocation typedPacket) {
        this.rawPacket = rawPacket;
        this.typedPacket = typedPacket;
    }

    public CompoundTag reset() {
        if (clean) return this.raw;

        this.raw = new CompoundTag();
        this.clean = true;
        this.immediate.clear();
        this.lazy.values().forEach(List::clear);

        return this.raw;
    }

    public void send(PacketSender sender, Player player) {
        if (!raw.isEmpty()) {
            var buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeNbt(raw);
            sender.send(rawPacket, buf);
        }

        immediate.forEach((type, data) -> {
            try {
                var buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeResourceLocation(Registrar.INSTANCE.dataType2Id.get(type));
                data.write(buf);
                sender.send(typedPacket, buf);
            } catch (Throwable t) {
                if (ExceptionUtil.dump(t, data.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                    player.sendSystemMessage(Component.literal("Error on retrieving data from provider " + data.getClass().getName()));
                }
            }
        });

        lazy.forEach((type, data) -> {
            var id = Registrar.INSTANCE.dataType2Id.get(type);

            final var finished = new boolean[]{false};
            for (var consumer : data) {
                try {
                    consumer.accept(new Result<>() {
                        boolean added = false;

                        @Override
                        public Result<IData> add(IData data) {
                            Preconditions.checkState(!added, "Called multiple times in the same closure");
                            Preconditions.checkNotNull(data, "Data is null");

                            var buf = new FriendlyByteBuf(Unpooled.buffer());
                            buf.writeResourceLocation(id);
                            data.write(buf);
                            sender.send(typedPacket, buf);

                            finished[0] = true;
                            added = true;

                            return this;
                        }

                        @Override
                        public Result<IData> block() {
                            finished[0] = true;
                            return this;
                        }
                    });
                } catch (Throwable t) {
                    if (ExceptionUtil.dump(t, consumer.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                        player.sendSystemMessage(Component.literal("Error on retrieving data from provider " + consumer.getClass().getName()));
                    }

                    finished[0] = true;
                }

                if (finished[0]) break;
            }
        });
    }

    @Override
    public CompoundTag raw() {
        clean = false;
        return raw;
    }

    @Override
    public <T extends IData> void add(Class<T> type, Consumer<Result<T>> consumer) {
        assertType(type);

        clean = false;
        lazy.computeIfAbsent(TypeUtil.uncheckedCast(type), t -> new ArrayList<>())
            .add(TypeUtil.uncheckedCast(consumer));
    }

    @Override
    public void addImmediate(IData data) {
        var type = data.getClass();

        assertType(type);
        if (lazy.containsKey(type) && !lazy.get(type).isEmpty()) throw new IllegalStateException("Data is already lazily added");

        clean = false;
        immediate.put(TypeUtil.uncheckedCast(type), data);
    }

    public <P, A> void tryAppend(Player player, P provider, A accessor, PluginConfig config, Provider<P, A> fn) {
        try {
            fn.write(provider, this, accessor, config);
        } catch (Throwable t) {
            if (ExceptionUtil.dump(t, provider.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                player.sendSystemMessage(Component.literal("Error on retrieving server data from provider " + provider.getClass().getName()));
            }
        }
    }

    private void assertType(Class<? extends IData> type) {
        Preconditions.checkArgument(Registrar.get().dataType2Id.containsKey(type), "Data type is not registered");
        Preconditions.checkState(!immediate.containsKey(type), "Data is already immediately added");
    }

    public interface Provider<P, A> {

        void write(P provider, DataWriter writer, A accessor, PluginConfig config);

    }

}
