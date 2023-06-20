package mcp.mobius.waila.util;

import mcp.mobius.waila.api.WailaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Log {

    private static final String PREFIX = "[" + WailaConstants.MOD_NAME + "] ";
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private final Logger logger;

    private Log(Logger logger) {
        this.logger = logger;
    }

    public static Log create() {
        return new Log(LoggerFactory.getLogger(STACK_WALKER.getCallerClass()));
    }

    public void info(String msg) {
        logger.info(PREFIX + msg);
    }

    public void info(String msg, Object arg) {
        logger.info(PREFIX + msg, arg);
    }

    public void info(String msg, Object arg1, Object arg2) {
        logger.info(PREFIX + msg, arg1, arg2);
    }

    public void warn(String msg) {
        logger.warn(PREFIX + msg);
    }

    public void error(String msg) {
        logger.error(PREFIX + msg);
    }

    public void error(String msg, Object arg) {
        logger.error(PREFIX + msg, arg);
    }

    public void error(String msg, Throwable t) {
        logger.error(PREFIX + msg, t);
    }

    public void error(String msg, Object arg1, Object arg2) {
        logger.error(PREFIX + msg, arg1, arg2);
    }

}
