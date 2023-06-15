package mcp.mobius.waila.plugin.forge.provider;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;

public enum ItemCapabilityProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.class, res ->
            accessor.getTarget().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                res.add(ItemData.of(config).getter(handler::getStackInSlot, handler.getSlots()))));
    }

}
