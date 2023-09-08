package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.data.ProgressData;
import net.minecraft.resources.ResourceLocation;

public class ProgressProvider extends DataProvider<ProgressData> {

    public static final ProgressProvider INSTANCE = new ProgressProvider();

    private ProgressProvider() {
        super(ProgressData.ID, ProgressData.class, ProgressData::new);
    }

    @Override
    protected void appendBody(ITooltip tooltip, ProgressData progress, IPluginConfig config, ResourceLocation objectId) {
        if (progress.ratio() == 0f) return;

        var line = tooltip.setLine(ProgressData.ID);

        for (var stack : progress.input()) {
            if (stack.isEmpty()) continue;
            line.with(new ItemComponent(stack));
        }

        line.with(new ProgressArrowComponent(progress.ratio()));

        for (var stack : progress.output()) {
            if (stack.isEmpty()) continue;
            line.with(new ItemComponent(stack));
        }
    }

}
