package mcp.mobius.waila.plugin.core.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.Registry;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;

public enum BlockProvider implements IBlockComponentProvider, IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        return new ItemComponent(accessor.getBlock().getCloneItemStack(accessor.getWorld(), accessor.getPosition(), accessor.getBlockState()));
    }

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockState().getMaterial().isLiquid()) {
            return;
        }

        var block = accessor.getBlock();
        var data = accessor.getData().raw();
        var name = block.getName().getString();

        if (data.contains("customName")) {
            name = data.getString("customName") + " (" + name + ")";
        }

        var formatter = IWailaConfig.get().getFormatter();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.blockName(name));
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(Registry.BLOCK.getKey(block)));
        }
    }

    @Override
    public void appendTail(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(IModInfo.get(accessor.getBlock()).getName()));
        }
    }

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
