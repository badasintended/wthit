package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.jaded.w2j.WServerEntityAccessor;
import net.minecraft.world.entity.Entity;
import snownee.jade.impl.WailaCommonRegistration;

public enum JEntityComponentProvider implements IEntityComponentProvider, IDataProvider<Entity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        var jadeAccessor = new WServerEntityAccessor(data, accessor);

        for (var provider : WailaCommonRegistration.instance().getEntityNBTProviders(accessor.getTarget())) {
            provider.appendServerData(data.raw(), jadeAccessor);
        }
    }

}
