package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;

public enum ContainerEntityProvider implements IDataProvider<Entity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof Container container) {
            data.add(ItemData.class, res -> res.add(ItemData
                .of(config)
                .vanilla(container)));
        }
    }

}
