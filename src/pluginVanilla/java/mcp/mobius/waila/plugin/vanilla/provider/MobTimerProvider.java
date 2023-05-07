package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AgeableMob;

public enum MobTimerProvider implements IEntityComponentProvider, IDataProvider<AgeableMob> {

    INSTANCE;

    private static final String TIMER = "%02d:%02d";

    private long lastAge;
    private long lastDataSync;

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getData().raw();
        if (data.contains("age")) {
            if (lastDataSync != accessor.getServerDataTime()) {
                lastDataSync = accessor.getServerDataTime();
                lastAge = data.getInt("age");
                long delay = (System.currentTimeMillis() - lastDataSync) / 50;
                if (lastAge < 0) {
                    lastAge += delay;
                } else if (lastAge > 0) {
                    lastAge -= delay;
                }
            }

            if (lastAge < 0 && config.getBoolean(Options.TIMER_GROW)) {
                long seconds = ((-lastAge) / 20) + 1;
                long minutes = seconds / 60;
                seconds = seconds - (minutes * 60);
                tooltip.addLine(new PairComponent(
                    Component.translatable(Tl.Tooltip.Timer.GROW),
                    Component.literal(TIMER.formatted(minutes, seconds))));
            }

            if (lastAge > 0 && config.getBoolean(Options.TIMER_BREED)) {
                long seconds = ((lastAge) / 20) + 1;
                long minutes = seconds / 60;
                seconds = seconds - (minutes * 60);
                tooltip.addLine(new PairComponent(
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

    @Override
    public void appendData(IDataWriter data, IServerAccessor<AgeableMob> accessor, IPluginConfig config) {
        AgeableMob mob = accessor.getTarget();
        data.raw().putInt("age", mob.getAge());
    }

}
