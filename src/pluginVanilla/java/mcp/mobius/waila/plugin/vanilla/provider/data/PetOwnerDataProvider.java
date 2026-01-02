package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;

public enum PetOwnerDataProvider implements IDataProvider<Entity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.PET_OWNER)) {
            var uuid = ((OwnableEntity) accessor.getTarget()).getOwnerUUID();
            if (uuid != null) data.raw().putUUID("owner", uuid);
        }
    }

}
