package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.plugin.extra.data.ProgressDataImpl;
import net.minecraft.resources.ResourceLocation;

public class ProgressProvider extends DataProvider<ProgressData, ProgressDataImpl> {

    public static final ProgressProvider INSTANCE = new ProgressProvider();

    private ProgressProvider() {
        super(ProgressData.ID, ProgressData.class, ProgressDataImpl.class, ProgressDataImpl::new);
    }

    @Override
    protected void appendBody(ITooltip tooltip, ProgressDataImpl progress, IPluginConfig config, ResourceLocation objectId) {
        if (progress.ratio() == 0f) return;

        var line = tooltip.setLine(ProgressData.ID);

        for (var stack : progress.input()) {
            line.with(new ItemComponent(stack));
        }

        line.with(new ProgressArrowComponent(progress.ratio()));

        for (var stack : progress.output()) {
            line.with(new ItemComponent(stack));
        }
    }

}
