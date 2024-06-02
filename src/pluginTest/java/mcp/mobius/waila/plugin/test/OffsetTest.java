package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ColorComponent;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.resources.ResourceLocation;

public enum OffsetTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:offset.enabled");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            tooltip.addLine(new PairComponent(
                new ColorComponent(35, 10, 0xFFAA0000),
                new ColorComponent(50, 20, 0xFF00AA00)));

            tooltip.addLine()
                .with(new ColorComponent(35, 20, 0xFF0000AA))
                .with(new ColorComponent(35, 10, 0xFFAA00AA));
        }
    }

}
