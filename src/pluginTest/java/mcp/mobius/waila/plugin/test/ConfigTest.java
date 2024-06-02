package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum ConfigTest implements IBlockComponentProvider {

    INSTANCE;

    static final ResourceLocation ENABLED = ResourceLocation.parse("test:enabled");

    static final ResourceLocation BOOL = ResourceLocation.parse("test:bool");
    static final ResourceLocation INT = ResourceLocation.parse("test:int");
    static final ResourceLocation DOUBLE = ResourceLocation.parse("test:double");
    static final ResourceLocation STRING = ResourceLocation.parse("test:string");
    static final ResourceLocation ENUM = ResourceLocation.parse("test:enum");

    static final ResourceLocation SYNC_BOOL = ResourceLocation.parse("test:sync_bool");
    static final ResourceLocation SYNC_INT = ResourceLocation.parse("test:sync_int");
    static final ResourceLocation SYNC_DOUBLE = ResourceLocation.parse("test:sync_double");
    static final ResourceLocation SYNC_STRING = ResourceLocation.parse("test:sync_string");
    static final ResourceLocation SYNC_ENUM = ResourceLocation.parse("test:sync_enum");

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!config.getBoolean(ENABLED)) {
            return;
        }

        tooltip.addLine(Component.literal(BOOL + "=" + config.getBoolean(BOOL)));
        tooltip.addLine(Component.literal(INT + "=" + config.getInt(INT)));
        tooltip.addLine(Component.literal(DOUBLE + "=" + config.getDouble(DOUBLE)));
        tooltip.addLine(Component.literal(STRING + "=" + config.getString(STRING)));
        tooltip.addLine(Component.literal(ENUM + "=" + config.getEnum(ENUM).name()));

        tooltip.addLine(Component.empty());

        tooltip.addLine(Component.literal(SYNC_BOOL + "=" + config.getBoolean(SYNC_BOOL)));
        tooltip.addLine(Component.literal(SYNC_INT + "=" + config.getInt(SYNC_INT)));
        tooltip.addLine(Component.literal(SYNC_DOUBLE + "=" + config.getDouble(SYNC_DOUBLE)));
        tooltip.addLine(Component.literal(SYNC_STRING + "=" + config.getString(SYNC_STRING)));
        tooltip.addLine(Component.literal(SYNC_ENUM + "=" + config.getEnum(SYNC_ENUM).name()));
    }

}
