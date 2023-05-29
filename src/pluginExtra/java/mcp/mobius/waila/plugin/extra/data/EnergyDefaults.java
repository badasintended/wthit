package mcp.mobius.waila.plugin.extra.data;

import java.util.HashMap;
import java.util.Map;

import mcp.mobius.waila.api.data.EnergyData;

public class EnergyDefaults implements EnergyData.Defaults {

    public static final Map<String, EnergyDefaults> MAP = new HashMap<>();
    private static final EnergyDefaults DEFAULT = new EnergyDefaults();

    private String nameTraslationKey = EnergyData.DEFAULT_TRANSLATION_KEY;
    private int color = EnergyData.DEFAULT_COLOR;
    private String unit = EnergyData.DEFAULT_UNIT;

    public static EnergyDefaults get(String namespace) {
        return MAP.getOrDefault(namespace, DEFAULT);
    }

    public String nameTraslationKey() {
        return nameTraslationKey;
    }

    public int color() {
        return color;
    }

    public String unit() {
        return unit;
    }

    @Override
    public EnergyDefaults nameTraslationKey(String nameTraslationKey) {
        this.nameTraslationKey = nameTraslationKey;
        return this;
    }

    @Override
    public EnergyDefaults color(int color) {
        this.color = color & 0xFFFFFF;
        return this;
    }

    @Override
    public EnergyDefaults unit(String unit) {
        this.unit = unit;
        return this;
    }

}
