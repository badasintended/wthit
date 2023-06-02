package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.component.BarComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.plugin.extra.data.EnergyDefaults;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyProvider extends DataProvider<EnergyData> {

    public static final EnergyProvider INSTANCE = new EnergyProvider();

    private static final String INFINITE = "∞";

    private EnergyProvider() {
        super(WailaConstants.ENERGY_TAG, EnergyData.class, EnergyData::new);
    }

    @Override
    protected void appendBody(ITooltip tooltip, EnergyData energy, IPluginConfig config, ResourceLocation objectId) {
        EnergyDefaults defaults = EnergyDefaults.get(objectId.getNamespace());

        long stored = energy.stored();
        long capacity = energy.capacity();
        float ratio = capacity == -1 ? 1f : (float) stored / capacity;

        String unit = energy.unit();
        if (unit == null) unit = defaults.unit();

        String nameTlKey = energy.nameTraslationKey();
        if (nameTlKey == null) nameTlKey = defaults.nameTraslationKey();

        int color = energy.color();
        if (color == -1) color = defaults.color();

        String text;
        if (stored == -1L) text = INFINITE;
        else {
            text = WailaHelper.suffix(stored);
            if (capacity != -1L) text += "/" + WailaHelper.suffix(capacity);
        }

        if (!unit.isEmpty()) text += " " + unit;

        tooltip.setLine(WailaConstants.ENERGY_TAG, new PairComponent(
            new WrappedComponent(Component.translatable(nameTlKey)),
            new BarComponent(ratio, 0xFF000000 | color, text)));
    }

}
