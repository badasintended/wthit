package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.resources.ResourceLocation;

public enum ErrorTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:error.enabled");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            throw new RuntimeException("ErrorTest");
        }
    }

}
