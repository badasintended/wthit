package mcp.mobius.waila.access;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
        public <D extends IData> @Nullable D get(IData.Type<D> type) {
            return null;
        }

        @Override
        public <D extends IData> void invalidate(IData.Type<D> type) {
        }
    };

    private CompoundTag raw;
    private final Map<ResourceLocation, IData> typed = new HashMap<>();
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

    @Override
    public CompoundTag raw() {
        return raw;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D extends IData> @Nullable D get(IData.Type<D> type) {
        return (D) typed.get(type.id());
    }

    @Override
    public <D extends IData> void invalidate(IData.Type<D> type) {
        typed.remove(type.id());
    }

    public void add(IData data) {
        clean = false;
        typed.put(data.type().id(), data);
    }

}
