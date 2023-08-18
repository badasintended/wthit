package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.FallingBlockEntity;

public enum FallingBlockProvider implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = accessor.getEntity();
        return new ItemComponent(entity.getBlockState().getBlock().asItem());
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        FallingBlockEntity entity = accessor.getEntity();
        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(entity.getBlockState().getBlock().getName().getString()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ENTITY_TYPE.getKey(accessor.getEntity().getType())));
        }
    }

}
