package mcp.mobius.waila.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionUtil {

    private static final ArrayList<String> ERRORS = new ArrayList<>();
    private static final File ERROR_OUTPUT = new File("WailaErrorOutput.txt");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");

    public static void dump(Throwable e, String className, List<Component> tooltip) {
        if (!ERRORS.contains(className)) {
            ERRORS.add(className);

            CommonUtil.LOGGER.error("Caught unhandled exception : [{}] {}", className, e);
            CommonUtil.LOGGER.error("See WailaErrorOutput.txt for more information");
            try {
                FileUtils.writeStringToFile(ERROR_OUTPUT, DATE_FORMAT.format(new Date()) + "\n" + className + "\n" + ExceptionUtils.getStackTrace(e) + "\n", StandardCharsets.UTF_8, true);
            } catch (Exception what) {
                // no
            }
        }
        if (tooltip != null)
            tooltip.add(new TextComponent("<ERROR>"));
    }

}
