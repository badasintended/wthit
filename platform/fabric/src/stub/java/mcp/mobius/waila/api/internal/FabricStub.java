package mcp.mobius.waila.api.internal;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricStub implements ModInitializer {

    @Override
    public void onInitialize() {
        throw new ApiJarInRuntimeException();
    }

}
