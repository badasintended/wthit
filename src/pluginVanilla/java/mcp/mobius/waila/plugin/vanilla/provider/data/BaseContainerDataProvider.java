package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.mixin.BaseContainerBlockEntityAccess;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

public enum BaseContainerDataProvider implements IDataProvider<BaseContainerBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BaseContainerBlockEntity> accessor, IPluginConfig config) {
        var target = (BaseContainerBlockEntityAccess) accessor.getTarget();
        if (!target.wthit_lockKey().unlocksWith(accessor.getPlayer().getMainHandItem())) {
            data.blockAll(ItemData.TYPE);
        }
    }

}
