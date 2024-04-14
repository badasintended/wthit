package mcp.mobius.waila.plugin.extra.data;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.data.EnergyData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class EnergyDataImpl extends EnergyData {

    public static final StreamCodec<RegistryFriendlyByteBuf, EnergyDataImpl> CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, EnergyDataImpl::stored,
        ByteBufCodecs.DOUBLE, EnergyDataImpl::capacity,
        EnergyDataImpl::new);

    private final double stored;
    private final double capacity;

    public EnergyDataImpl(double stored, double capacity) {
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public Type<? extends IData> type() {
        return TYPE;
    }

    public double stored() {
        return stored;
    }

    public double capacity() {
        return capacity;
    }

    public static class Description implements EnergyData.Description {

        public static final Map<String, Description> MAP = new HashMap<>();
        private static final Description DEFAULT = new Description();

        private Component name = DEFAULT_NAME;
        private int color = DEFAULT_COLOR;
        private String unit = DEFAULT_UNIT;

        public static Description get(String namespace) {
            return MAP.getOrDefault(namespace, DEFAULT);
        }

        public Component name() {
            return name;
        }

        public int color() {
            return color;
        }

        public String unit() {
            return unit;
        }

        @Override
        public Description name(Component name) {
            this.name = name;
            return this;
        }

        @Override
        public Description color(int color) {
            this.color = color & 0xFFFFFF;
            return this;
        }

        @Override
        public Description unit(String unit) {
            this.unit = unit;
            return this;
        }

    }

}
