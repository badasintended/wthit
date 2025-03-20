package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;

public enum MobTimerProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final String TIMER = "%02d:%02d";

    private long lastAge;
    private long lastDataSync;

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var data = accessor.getData().raw();
        if (data.contains("age")) {
            if (lastDataSync != accessor.getServerDataTime()) {
                lastDataSync = accessor.getServerDataTime();
                lastAge = data.getInt("age").orElseThrow();
                var delay = (System.currentTimeMillis() - lastDataSync) / 50;
                if (lastAge < 0) {
                    lastAge += delay;
                } else if (lastAge > 0) {
                    lastAge -= delay;
                }
            }

            if (lastAge < 0 && config.getBoolean(Options.TIMER_GROW)) {
                var seconds = ((-lastAge) / 20) + 1;
                var minutes = seconds / 60;
                seconds = seconds - (minutes * 60);
                tooltip.setLine(Options.TIMER_GROW, new PairComponent(
                    Component.translatable(Tl.Tooltip.Timer.GROW),
                    Component.literal(TIMER.formatted(minutes, seconds))));
            }

            if (lastAge > 0 && config.getBoolean(Options.TIMER_BREED)) {
                var seconds = ((lastAge) / 20) + 1;
                var minutes = seconds / 60;
                seconds = seconds - (minutes * 60);
                tooltip.setLine(Options.TIMER_BREED, new PairComponent(
                    Component.translatable(Tl.Tooltip.Timer.BREED),
                    Component.literal(TIMER.formatted(minutes, seconds))));
            }

            if (lastAge < 0) {
                lastAge++;
            } else if (lastAge > 0) {
                lastAge--;
            }
        }
    }

}
