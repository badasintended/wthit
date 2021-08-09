package mcp.mobius.waila.util;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CommonUtil {

    public static final Logger LOGGER = LogManager.getLogger("Waila");

    public static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE, path);
    }

}
