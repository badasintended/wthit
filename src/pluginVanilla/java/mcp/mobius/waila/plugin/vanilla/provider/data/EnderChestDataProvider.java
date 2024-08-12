package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ItemData;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public enum EnderChestDataProvider implements IDataProvider<EnderChestBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<EnderChestBlockEntity> accessor, IPluginConfig config) {
        data.add(ItemData.TYPE, res -> res.add(ItemData
            .of(config)
            .vanilla(accessor.getPlayer().getEnderChestInventory())));
    }

}
