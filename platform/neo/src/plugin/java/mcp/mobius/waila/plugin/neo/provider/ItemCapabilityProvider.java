package mcp.mobius.waila.plugin.neo.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;

public enum ItemCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.class, res ->
            accessor.getTarget().getCapability(Capabilities.ITEM_HANDLER).ifPresent(handler ->
                res.add(ItemData.of(config).getter(handler::getStackInSlot, handler.getSlots()))));
    }

}
