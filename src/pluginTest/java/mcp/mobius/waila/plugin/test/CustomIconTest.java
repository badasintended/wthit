package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ColorComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public enum CustomIconTest implements IBlockComponentProvider {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:custom_icon.enabled");

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        var random = (int) Mth.randomBetween(accessor.getWorld().getRandom(), 10, 30);
        return config.getBoolean(ENABLED) ? new ColorComponent(random, random, 0xFF770000) : null;
    }

}
