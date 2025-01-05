package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.PositionComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import mcp.mobius.waila.plugin.vanilla.provider.data.BeeDataProvider;
import net.minecraft.network.chat.Component;

public enum BeeProvider implements IEntityComponentProvider {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var hivePos = accessor.getData().get(BeeDataProvider.HIVE_POS);
        if (hivePos != null && config.getBoolean(Options.BEE_HIVE_POS)) {
            tooltip.setLine(Options.BEE_HIVE_POS, new PairComponent(
                new WrappedComponent(Component.translatable(Tl.Tooltip.Bee.HIVE)),
                new PositionComponent(hivePos.pos())));
        }
    }

}
