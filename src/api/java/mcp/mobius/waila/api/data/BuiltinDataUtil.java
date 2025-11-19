package mcp.mobius.waila.api.data;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/* package-private */ abstract class BuiltinDataUtil {

    @ApiStatus.Internal
    static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(WailaConstants.NAMESPACE + "x", path);
    }

    private BuiltinDataUtil() {
        throw new IllegalCallerException();
    }

}
