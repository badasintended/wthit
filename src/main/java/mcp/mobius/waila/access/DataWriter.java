package mcp.mobius.waila.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.play.c2s.RawDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.c2s.TypedDataRequestContextPlayC2SPacket;
import mcp.mobius.waila.network.play.s2c.RawDataResponsePlayS2CPacket;
import mcp.mobius.waila.network.play.s2c.TypedDataResponsePlayS2CPacket;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public enum DataWriter implements IDataWriter {

    SERVER(RawDataResponsePlayS2CPacket.Payload::new, TypedDataResponsePlayS2CPacket.Payload::new),
    CLIENT(RawDataRequestContextPlayC2SPacket.Payload::new, TypedDataRequestContextPlayC2SPacket.Payload::new);

    private final Function<CompoundTag, CustomPacketPayload> rawPacket;
    private final Function<IData, CustomPacketPayload> typedPacket;

    private final Map<IData.Type<IData>, IData> immediate = new HashMap<>();
    private final Map<IData.Type<IData>, List<Consumer<Result<IData>>>> lazy = new HashMap<>();

    private CompoundTag raw;
    private boolean clean;

    DataWriter(Function<CompoundTag, CustomPacketPayload> rawPacket, Function<IData, CustomPacketPayload> typedPacket) {
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
        if (!raw.isEmpty()) sender.send(rawPacket.apply(raw));

        immediate.values().forEach(data -> {
            try {
                sender.send(typedPacket.apply(data));
            } catch (Throwable t) {
                if (ExceptionUtil.dump(t, data.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                    player.displayClientMessage(Component.literal("Error on retrieving data from provider " + data.getClass().getName()), false);
                }
            }
        });

        lazy.forEach((type, data) -> {
            final var finished = new boolean[]{false};
            for (var consumer : data) {
                try {
                    consumer.accept(new Result<>() {
                        boolean added = false;

                        @Override
                        public Result<IData> add(IData data) {
                            Preconditions.checkState(!added, "Called multiple times in the same closure");
                            Preconditions.checkNotNull(data, "Data is null");

                            sender.send(typedPacket.apply(data));

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
                        player.displayClientMessage(Component.literal("Error on retrieving data from provider " + consumer.getClass().getName()), false);
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
    public <D extends IData> void add(IData.Type<D> type, Consumer<Result<D>> consumer) {
        assertType(type);

        clean = false;
        lazy.computeIfAbsent(TypeUtil.uncheckedCast(type), t -> new ArrayList<>())
            .add(TypeUtil.uncheckedCast(consumer));
    }

    @Override
    public void addImmediate(IData data) {
        var type = data.type();

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
                player.displayClientMessage(Component.literal("Error on retrieving server data from provider " + provider.getClass().getName()), false);
            }
        }
    }

    private void assertType(IData.Type<? extends IData> type) {
        Preconditions.checkArgument(Registrar.get().dataCodecs.containsKey(type.id()), "Data type is not registered");
        Preconditions.checkState(!immediate.containsKey(type), "Data is already immediately added");
    }

    public interface Provider<P, A> {

        void write(P provider, DataWriter writer, A accessor, PluginConfig config);

    }

}
