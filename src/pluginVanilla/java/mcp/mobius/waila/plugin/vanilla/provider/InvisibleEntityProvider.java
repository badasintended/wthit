package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public enum InvisibleEntityProvider implements IEntityComponentProvider {

    INSTANCE;

    @Nullable
    @Override
    public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.OVERRIDE_INVISIBLE_ENTITY)) {
            LivingEntity entity = accessor.getEntity();

            if (entity.isInvisibleTo(accessor.getPlayer())) {
                return EMPTY_ENTITY;
            }
        }

        return null;
    }

}
