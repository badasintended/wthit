package mcp.mobius.waila.api.data;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/* package-private */ abstract class BuiltinDataUtil {

    @ApiStatus.Internal
    static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE + "x", path);
    }

    private BuiltinDataUtil() {
        throw new IllegalCallerException();
    }

}
