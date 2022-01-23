package mcp.mobius.waila.api.__internal__;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricStub implements ModInitializer {

    @Override
    public void onInitialize() {
        throw new ApiJarInRuntimeException();
    }

}
