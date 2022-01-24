package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
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
        IWailaConfig.Formatting formatting = IWailaConfig.get().getFormatting();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, new TextComponent(formatting.formatEntityName(entity.getBlockState().getBlock().getName().getContents())));
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY))
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, new TextComponent(formatting.formatRegistryName(Registry.ENTITY_TYPE.getKey(accessor.getEntity().getType()))));
    }

}
