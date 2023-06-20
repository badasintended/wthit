package mcp.mobius.waila.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionUtil {

    private static final Log LOG = Log.create();
    private static final Set<String> ERRORS = new HashSet<>();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
    private static final File ERROR_FILE = Waila.GAME_DIR.resolve("logs/waila_errors.log").toFile();

    static {
        // noinspection ResultOfMethodCallIgnored
        ERROR_FILE.getParentFile().mkdirs();
    }

    public static void dump(Throwable e, String errorName, ITooltip tooltip) {
        if (ERRORS.add(errorName)) {
            LOG.error("Caught unhandled exception : [{}] {}", errorName, e);
            LOG.error("See {} for more information", ERROR_FILE);

            try (FileWriter writer = new FileWriter(ERROR_FILE, StandardCharsets.UTF_8)) {
                writer.write(DATE_FORMAT.format(new Date()) + "\n" + errorName + "\n" + ExceptionUtils.getStackTrace(e) + "\n");
            } catch (IOException ioException) {
                // no-op
            }
        }
        if (tooltip != null) {
            tooltip.addLine(Component.literal("Error on " + errorName).withStyle(ChatFormatting.RED));
            tooltip.addLine(Component.literal("See logs/waila_errors.log for more info").withStyle(ChatFormatting.RED));
        }
    }

}
