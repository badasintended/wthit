package mcp.mobius.waila.api.__internal__;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

@ApiStatus.Internal
public class QuiltStub implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        throw new ApiJarInRuntimeException();
    }

}
