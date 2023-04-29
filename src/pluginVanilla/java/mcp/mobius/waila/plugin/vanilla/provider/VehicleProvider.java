package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public enum VehicleProvider implements IEntityComponentProvider {

    INSTANCE;

    @Nullable
    @Override
    public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        return accessor.getPlayer().getVehicle() == accessor.getEntity() && config.getBoolean(Options.OVERRIDE_HIDE_VEHICLE) ? EMPTY_ENTITY : null;
    }

}
