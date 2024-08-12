package mcp.mobius.waila.plugin.core.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;

public enum NameableBlockDataProvider implements IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof Nameable nameable) {
            var name = nameable.getCustomName();
            if (name != null) {
                data.raw().putString("customName", name.getString());
            }
        }
    }

}
