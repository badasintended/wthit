package mcp.mobius.waila.util;

import java.util.HashSet;
import java.util.Set;

import mcp.mobius.waila.api.ITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionUtil {

    private static final Log LOG = Log.create();
    private static final Set<String> ERRORS = new HashSet<>();

    public static boolean dump(Throwable e, String errorName, ITooltip tooltip) {
        var log = ERRORS.add(errorName);

        if (log) {
            LOG.error("Caught unhandled exception : [{}] {}", errorName, e);
            LOG.error(ExceptionUtils.getStackTrace(e));
        }

        if (tooltip != null) {
            tooltip.addLine(Component.literal("Error on " + errorName).withStyle(ChatFormatting.RED));
            tooltip.addLine(Component.literal("See logs for more info").withStyle(ChatFormatting.RED));
        }

        return log;
    }

}
