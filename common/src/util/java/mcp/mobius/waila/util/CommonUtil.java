package mcp.mobius.waila.util;

import java.nio.file.Path;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CommonUtil {

    public static final Logger LOGGER = LogManager.getLogger(WailaConstants.MOD_NAME);

    public static Path gameDir;
    public static Path configDir;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE, path);
    }

}
