package mcp.mobius.waila.plugin.core.provider;

import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITargetRedirector;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.mixin.EntityAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public enum EntityProvider implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public @Nullable ITargetRedirector.Result redirect(ITargetRedirector redirect, IEntityAccessor accessor, IPluginConfig config) {
        if (IBlacklistConfig.get().contains(accessor.getEntity())) return redirect.toBehind();

        return null;
    }

    @Nullable
    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        var entity = accessor.getEntity();
        if (entity instanceof Mob) {
            return null;
        }

        var stack = entity.getPickResult();
        return stack != null ? new ItemComponent(stack) : null;
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var entity = accessor.getEntity();
        var formatter = IWailaConfig.get().getFormatter();

        String name;
        var customName = entity.getCustomName();
        if (customName != null) {
            name = customName.getString() + " (" + ((EntityAccess) entity).wthit_getTypeName().getString() + ")";
        } else {
            name = entity.getName().getString();
        }

        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(name));
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())));
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(accessor.getEntity()).getName()));
        }
    }

}
