package mcp.mobius.waila.access;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public enum DataReader implements IDataReader {

    INSTANCE;

    private CompoundTag raw;
    private final Map<Class<? extends IData>, IData> typed = new HashMap<>();
    private boolean clean;

    public void reset(@Nullable CompoundTag raw) {
        if (clean && raw == null) return;

        this.raw = raw == null ? new CompoundTag() : raw;
        this.clean = raw == null;

        this.typed.clear();
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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IData> @Nullable T get(Class<T> type) {
        return (T) typed.get(type);
    }

    public void add(IData data) {
        clean = false;
        typed.put(data.getClass(), data);
    }

}
