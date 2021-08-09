package mcp.mobius.waila.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionUtil {

    private static final ArrayList<String> ERRORS = new ArrayList<>();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");

    public static void dump(Throwable e, String className, List<Component> tooltip) {
        if (!ERRORS.contains(className)) {
            ERRORS.add(className);

            CommonUtil.LOGGER.error("Caught unhandled exception : [{}] {}", className, e);
            CommonUtil.LOGGER.error("See .waila/WailaErrorOutput.txt for more information");

            try (FileWriter writer = new FileWriter(CommonUtil.gameDir.resolve(".waila/WailaErrorOutput.txt").toFile())) {
                writer.write(DATE_FORMAT.format(new Date()) + "\n" + className + "\n" + ExceptionUtils.getStackTrace(e) + "\n");
            } catch (IOException ioException) {
                // no-op
            }
        }
        if (tooltip != null)
            tooltip.add(new TextComponent("<ERROR>"));
    }

}
