package mcp.mobius.waila.access;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.network.Packets;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public enum DataRW implements IDataReader, IDataWriter {

    INSTANCE;

    private CompoundTag raw;
    private Map<Class<? extends IData>, IData> typed;
    private boolean clean;

    public CompoundTag reset(@Nullable CompoundTag raw) {
        if (clean) return this.raw;

        this.raw = raw == null ? new CompoundTag() : raw;
        this.typed = new HashMap<>();
        this.clean = raw == null;

        return this.raw;
    }

    public void sendTypedPackets(PacketSender sender) {
        for (IData data : typed.values()) {
            ResourceLocation id = Registrar.INSTANCE.dataType2Id.get(data.getClass());

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(id);
            data.write(buf);

            sender.send(Packets.DATA_TYPED, buf);
        }
    }

    @SuppressWarnings("unchecked")
    public static IData readTypedPacket(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        IData.Serializer<IData> serializer = (IData.Serializer<IData>) Registrar.INSTANCE.dataId2Serializer.get(id);

        return serializer.read(buf);
    }

    @Override
    public CompoundTag raw() {
        return raw;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IData> T get(Class<T> type) {
        return (T) typed.get(type);
    }

    @Override
    public <T extends IData> void add(T data) {
        clean = false;
        typed.put(data.getClass(), data);
    }

}
