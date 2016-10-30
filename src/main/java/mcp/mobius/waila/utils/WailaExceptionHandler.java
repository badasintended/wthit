package mcp.mobius.waila.utils;

import mcp.mobius.waila.Waila;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class WailaExceptionHandler {

    private static ArrayList<String> errs = new ArrayList<String>();

    public WailaExceptionHandler() {
    }

    public static List<String> handleErr(Throwable e, String className, List<String> currenttip) {
        if (!errs.contains(className)) {
            errs.add(className);

            for (StackTraceElement elem : e.getStackTrace()) {
                Waila.LOGGER.log(Level.WARN, String.format("%s.%s:%s", elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
                if (elem.getClassName().contains("waila")) break;
            }

            Waila.LOGGER.log(Level.WARN, String.format("Catched unhandled exception : [%s] %s", className, e));
        }
        if (currenttip != null)
            currenttip.add("<ERROR>");

        return currenttip;
    }

}
