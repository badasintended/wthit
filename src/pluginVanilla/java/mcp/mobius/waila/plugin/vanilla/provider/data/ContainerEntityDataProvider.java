package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;

public enum ContainerEntityDataProvider implements IDataProvider<Entity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        data.add(ItemData.TYPE, res -> {
            var entity = accessor.getTarget();

            if (entity instanceof ContainerEntity container && container.getContainerLootTable() != null) {
                res.block();
                return;
            }

            res.add(ItemData.of(config).vanilla((Container) entity));
        });
    }

}
