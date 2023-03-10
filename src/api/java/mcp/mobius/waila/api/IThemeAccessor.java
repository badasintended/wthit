package mcp.mobius.waila.api;

import java.nio.file.Path;

import mcp.mobius.waila.api.__internal__.ApiSide;
import org.jetbrains.annotations.ApiStatus;

@ApiSide.ClientOnly
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface IThemeAccessor {

    /**
     * Resolves path relative to the waila configuration file, useful for themes that need access to external assets.
     */
    Path getPath(String path);

}
