package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public enum ConfigTest implements IBlockComponentProvider {

    INSTANCE;

    static final Identifier ENABLED = Identifier.parse("test:enabled");

    static final Identifier BOOL = Identifier.parse("test:bool");
    static final Identifier INT = Identifier.parse("test:int");
    static final Identifier DOUBLE = Identifier.parse("test:double");
    static final Identifier STRING = Identifier.parse("test:string");
    static final Identifier ENUM = Identifier.parse("test:enum");

    static final Identifier SYNC_BOOL = Identifier.parse("test:sync_bool");
    static final Identifier SYNC_INT = Identifier.parse("test:sync_int");
    static final Identifier SYNC_DOUBLE = Identifier.parse("test:sync_double");
    static final Identifier SYNC_STRING = Identifier.parse("test:sync_string");
    static final Identifier SYNC_ENUM = Identifier.parse("test:sync_enum");

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
