package mcp.mobius.waila.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.s2c.TypedDataResponseS2CPacket;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.ExceptionUtil;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public enum DataWriter implements IDataWriter {

    INSTANCE;

    private CompoundTag raw;
    private final Map<Class<IData>, List<Consumer<Result<IData>>>> typed = new HashMap<>();
    private boolean clean;

    public CompoundTag reset() {
        if (clean) return this.raw;

        this.raw = new CompoundTag();
        this.clean = true;
        this.typed.values().forEach(List::clear);

        return this.raw;
    }

    public void sendTypedPackets(PacketSender sender, ServerPlayer player) {
        typed.forEach((type, data) -> {
            final boolean[] finished = {false};
            for (Consumer<Result<IData>> consumer : data) {
                try {
                    consumer.accept(new Result<>() {
                        boolean added = false;

                        @Override
                        public Result<IData> add(IData data) {
                            Preconditions.checkState(!added, "Called multiple times in the same closure");
                            Preconditions.checkNotNull(data, "Data is null");

                            sender.send(new TypedDataResponseS2CPacket.Payload(data));

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
                        player.sendSystemMessage(Component.literal("Error on retrieving server data from provider " + consumer.getClass().getName()));
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
        Preconditions.checkArgument(Registrar.INSTANCE.dataType2Id.containsKey(type), "Data type is not registered");

        clean = false;
        typed.computeIfAbsent(TypeUtil.uncheckedCast(type), t -> new ArrayList<>())
            .add(TypeUtil.uncheckedCast(consumer));
    }

    public <T> void tryAppendData(IDataProvider<T> provider, IServerAccessor<T> accessor) {
        try {
            provider.appendData(this, accessor, PluginConfig.SERVER);
        } catch (Throwable t) {
            ServerPlayer player = accessor.getPlayer();

            if (ExceptionUtil.dump(t, provider.getClass() + "\nplayer " + player.getScoreboardName(), null)) {
                player.sendSystemMessage(Component.literal("Error on retrieving server data from provider " + provider.getClass().getName()));
            }
        }
    }

}
