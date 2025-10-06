package mcp.mobius.waila.plugin.extra.provider;

import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.extra.data.ProgressDataImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ProgressProvider extends DataProvider<ProgressData, ProgressDataImpl> {

    public static final ProgressProvider INSTANCE = new ProgressProvider();

    private static final Component ESTIMATED_TIME = Component.translatable(Tl.Tooltip.Extra.ESTIMATED_TIME);
    private static final String TIMER = "%02d:%02d";

    private ProgressProvider() {
        super(ProgressData.TYPE, ProgressDataImpl.CODEC);
    }

    @Override
    protected void registerAdditions(ICommonRegistrar registrar, int priority) {
        registrar.featureConfig(ProgressData.CONFIG_TIME, false);
    }

    private void appendProgress(ITooltip tooltip, ProgressDataImpl progress, float ratio) {
        var line = tooltip.setLine(ProgressData.ID);

        for (var stack : progress.input()) {
            if (stack.isEmpty()) continue;
            line.with(new ItemComponent(stack));
        }

        line.with(new ProgressArrowComponent(ratio));

        for (var stack : progress.output()) {
            if (stack.isEmpty()) continue;
            line.with(new ItemComponent(stack));
        }
    }

    @Override
    protected void appendBody(ITooltip tooltip, ProgressDataImpl progress, IPluginConfig config, ResourceLocation objectId) {
        if (progress.hasTick) {
            var current = progress.currentTick;
            var max = progress.maxTick;
            var remaining = max - current;
            if (current == 0 || remaining >= max) return;
            appendProgress(tooltip, progress, (float) current / max);

            if (config.getBoolean(ProgressData.CONFIG_TIME)) {
                var seconds = ((remaining) / 20) + 1;
                var minutes = seconds / 60;
                seconds = seconds % 60;
                tooltip.setLine(ProgressData.CONFIG_TIME, new PairComponent(
                    ESTIMATED_TIME,
                    Component.literal(TIMER.formatted(minutes, seconds))));
            }

            progress.currentTick++;
        } else {
            if (progress.ratio == 0f) return;
            appendProgress(tooltip, progress, progress.ratio);
        }
    }

}
