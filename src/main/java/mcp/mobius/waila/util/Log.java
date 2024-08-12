package mcp.mobius.waila.util;

import mcp.mobius.waila.api.WailaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("StringConcatenationArgumentToLogCall")
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

    public void warn(String msg, Object arg) {
        logger.warn(PREFIX + msg, arg);
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

    public void error(String msg, Object... arg) {
        logger.error(PREFIX + msg, arg);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(String msg) {
        logger.debug(PREFIX + msg);
    }

    public void debug(String msg, Object arg) {
        logger.debug(PREFIX + msg, arg);
    }

    public void debug(String msg, Object arg1, Object arg2) {
        logger.debug(PREFIX + msg, arg1, arg2);
    }

}
