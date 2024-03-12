package mcp.mobius.waila.jaded.j2w;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.jaded.w2j.WClientBlockAccessor;
import mcp.mobius.waila.jaded.w2j.WServerBlockAccessor;
import mcp.mobius.waila.jaded.w2j.WTooltip;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.WailaCommonRegistration;
import snownee.jade.impl.config.PluginConfig;

public enum JBlockComponentProvider implements IBlockComponentProvider, IDataProvider<BlockEntity> {

    INSTANCE;

    @Override
    public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        var jadeAccessor = new WClientBlockAccessor(accessor);

        for (var provider : WailaClientRegistration.instance().getBlockIconProviders(accessor.getBlock(), PluginConfig.INSTANCE::get)) {
            var res = provider.getIcon(jadeAccessor, PluginConfig.INSTANCE, null);
            if (res != null) {
                return new JComponent(res);
            }
        }

        return null;
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var jadeAccessor = new WClientBlockAccessor(accessor);
        var jadeTooltip = new WTooltip(tooltip);

        for (var provider : WailaClientRegistration.instance().getBlockProviders(accessor.getBlock(), PluginConfig.INSTANCE::get)) {
            provider.appendTooltip(jadeTooltip, jadeAccessor, PluginConfig.INSTANCE);
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        var jadeAccessor = new WServerBlockAccessor(data, accessor);

        for (var provider : WailaCommonRegistration.instance().getBlockNBTProviders(accessor.getTarget())) {
            provider.appendServerData(data.raw(), jadeAccessor);
        }
    }

}
