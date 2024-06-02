package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ColorComponent;
import net.minecraft.resources.ResourceLocation;

public enum LongTest implements IBlockComponentProvider {

    INSTANCE;

    static final ResourceLocation ENABLED = ResourceLocation.parse("test:long.enabled");
    static final ResourceLocation WIDTH = ResourceLocation.parse("test:long.width");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            var width = config.getInt(WIDTH);
            tooltip.addLine(new ColorComponent(width, 10, 0xFFFF00FF));
        }
    }

}
