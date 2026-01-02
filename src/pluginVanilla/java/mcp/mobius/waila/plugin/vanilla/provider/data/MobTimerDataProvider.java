package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.world.entity.AgeableMob;

public enum MobTimerDataProvider implements IDataProvider<AgeableMob> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<AgeableMob> accessor, IPluginConfig config) {
        var mob = accessor.getTarget();
        data.raw().putInt("age", mob.getAge());
    }

}
