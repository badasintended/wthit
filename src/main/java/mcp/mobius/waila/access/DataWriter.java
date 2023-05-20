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
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.util.TypeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

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

    public void sendTypedPackets(PacketSender sender) {
        typed.forEach((type, data) -> {
            ResourceLocation id = Registrar.INSTANCE.dataType2Id.get(type);

            final boolean[] finished = {false};
            for (Consumer<Result<IData>> consumer : data) {
                consumer.accept(new Result<>() {
                    boolean added = false;

                    @Override
                    public Result<IData> add(IData data) {
                        Preconditions.checkState(!added, "Called multiple times in the same closure");
                        Preconditions.checkNotNull(data, "Data is null");

                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                        buf.writeResourceLocation(id);
                        data.write(buf);
                        sender.send(Packets.DATA_TYPED, buf);

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

}
