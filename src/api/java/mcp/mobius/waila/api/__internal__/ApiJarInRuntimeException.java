package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.WailaConstants;
import org.jetbrains.annotations.ApiStatus;

/**
 * Thrown when the API-only jar is in runtime classpath.
 */
@ApiStatus.Internal
public class ApiJarInRuntimeException extends RuntimeException {

    public ApiJarInRuntimeException() {
        super("ONLY USE " + WailaConstants.MOD_NAME + " API JAR AS A COMPILE ONLY DEPENDENCY");
    }

}
