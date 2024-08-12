package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public enum EntityAttributesDataProvider implements IDataProvider<Entity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Entity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof LivingEntity living) {
            if (config.getBoolean(Options.ENTITY_ABSORPTION) && living.getAbsorptionAmount() > 0) {
                data.raw().putFloat("abs", living.getAbsorptionAmount());
            }
        }
    }

}
