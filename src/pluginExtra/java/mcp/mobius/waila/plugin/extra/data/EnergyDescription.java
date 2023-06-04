package mcp.mobius.waila.plugin.extra.data;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.data.EnergyData;
import net.minecraft.network.chat.Component;

public class EnergyDescription implements EnergyData.Description {

    public static final Map<String, EnergyDescription> MAP = new HashMap<>();
    private static final EnergyDescription DEFAULT = new EnergyDescription();

    private Component name = EnergyData.DEFAULT_NAME;
    private int color = EnergyData.DEFAULT_COLOR;
    private String unit = EnergyData.DEFAULT_UNIT;

    public static EnergyDescription get(String namespace) {
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
    public EnergyDescription name(Component name) {
        this.name = name;
        return this;
    }

    @Override
    public EnergyDescription color(int color) {
        this.color = color & 0xFFFFFF;
        return this;
    }

    @Override
    public EnergyDescription unit(String unit) {
        this.unit = unit;
        return this;
    }

}
