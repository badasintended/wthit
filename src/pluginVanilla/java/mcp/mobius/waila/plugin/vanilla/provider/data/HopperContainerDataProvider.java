package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public enum HopperContainerDataProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.TYPE, res -> {
            var container = HopperBlockEntity.getContainerAt(accessor.getWorld(), accessor.getTarget().getBlockPos());
            if (container != null) res.add(ItemData.of(config).vanilla(container));
        });
    }

}
