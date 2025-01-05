package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.ComposterBlock;

public enum ComposterProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.LEVEL_COMPOSTER)) {
            var state = accessor.getBlockState();
            tooltip.setLine(Options.LEVEL_COMPOSTER, new PairComponent(
                Component.translatable(Tl.Tooltip.COMPOST_LEVEL),
                Component.literal(state.getValue(ComposterBlock.LEVEL).toString())));
        }
    }

}
