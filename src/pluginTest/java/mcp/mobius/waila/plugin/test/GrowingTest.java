package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipLine;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ColorComponent;
import mcp.mobius.waila.api.component.GrowingComponent;
import net.minecraft.resources.ResourceLocation;

public enum GrowingTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = new ResourceLocation("test:grow");

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            ITooltipLine line = tooltip.getLine(WailaConstants.OBJECT_NAME_TAG);

            if (line != null) {
                line.with(GrowingComponent.INSTANCE)
                    .with(new ColorComponent(10, 10, 0x55FF00FF));
            }
        }
    }

}
