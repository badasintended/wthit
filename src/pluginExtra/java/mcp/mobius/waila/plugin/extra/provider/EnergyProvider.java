package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.component.BarComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.api.data.EnergyData;
import mcp.mobius.waila.plugin.extra.data.EnergyDescription;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyProvider extends DataProvider<EnergyData> {

    public static final EnergyProvider INSTANCE = new EnergyProvider();

    private static final String INFINITE = "∞";

    private EnergyProvider() {
        super(EnergyData.ID, EnergyData.class, EnergyData::new);
    }

    @Override
    protected void appendBody(ITooltip tooltip, EnergyData energy, IPluginConfig config, ResourceLocation objectId) {
        EnergyDescription desc = EnergyDescription.get(objectId.getNamespace());

        double stored = energy.stored();
        double capacity = energy.capacity();
        float ratio = Double.isInfinite(capacity) ? 1f : (float) (stored / capacity);

        String unit = desc.unit();
        Component name = desc.name();
        int color = desc.color();

        String text;
        if (Double.isInfinite(stored)) text = INFINITE;
        else {
            text = WailaHelper.suffix((long) stored);
            if (Double.isFinite(capacity)) text += "/" + WailaHelper.suffix((long) capacity);
        }

        if (!unit.isEmpty()) text += " " + unit;

        tooltip.setLine(EnergyData.ID, new PairComponent(
            new WrappedComponent(name),
            new BarComponent(ratio, 0xFF000000 | color, text)));
    }

}
