package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.component.BarComponent;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import net.minecraft.resources.ResourceLocation;

public enum BarTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = new ResourceLocation("test:bar.enabld");

    private int tick = 0;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            tick++;
            if (tick == 500) {
                tick = 0;
            }

            tooltip.addLine(new PairComponent(
                new WrappedComponent("bar"),
                new BarComponent(tick / 500f, 0x88AA0000, WailaHelper.suffix(tick * 10L))));
        }
    }

}
