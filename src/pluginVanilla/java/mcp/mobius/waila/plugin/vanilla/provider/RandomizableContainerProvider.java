package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import mcp.mobius.waila.mixin.RandomizableContainerBlockEntityAccess;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public enum RandomizableContainerProvider implements IDataProvider<RandomizableContainerBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<RandomizableContainerBlockEntity> accessor, IPluginConfig config) {
        if (((RandomizableContainerBlockEntityAccess) accessor.getTarget()).wthit_lootTable() != null) {
            data.add(ItemData.class, IDataWriter.Result::block);
        }
    }

}
