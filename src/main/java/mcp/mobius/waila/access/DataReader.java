package mcp.mobius.waila.access;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataReader;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public enum DataReader implements IDataReader {

    SERVER, CLIENT;

    public static final IDataReader NOOP = new IDataReader() {
        private static final CompoundTag TAG = new CompoundTag();

        @Override
        public CompoundTag raw() {
            return TAG;
        }

        @Override
        public <T extends IData> @Nullable T get(Class<T> type) {
            return null;
        }

        @Override
        public <T extends IData> void invalidate(Class<T> type) {
        }
    };

    private CompoundTag raw;
    private final Map<Class<? extends IData>, IData> typed = new HashMap<>();
    private boolean clean;

    DataReader() {
        reset(null);
    }

    public void reset(@Nullable CompoundTag raw) {
        if (clean && raw == null) return;

        this.raw = raw == null ? new CompoundTag() : raw;
        this.clean = raw == null;

        this.typed.clear();
    }

    @SuppressWarnings("unchecked")
    public static IData readTypedPacket(FriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var serializer = (IData.Serializer<IData>) Registrar.get().dataId2Serializer.get(id);

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

    @Override
    public <T extends IData> void invalidate(Class<T> type) {
        typed.remove(type);
    }

    public void add(IData data) {
        clean = false;

        typed.put(Registrar.get().impl2ApiDataType.get(data.getClass()), data);
    }

}
