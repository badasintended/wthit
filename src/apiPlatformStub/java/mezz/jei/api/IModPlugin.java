package mezz.jei.api;

import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

public interface IModPlugin {

    Identifier getPluginUid();

    default void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

}
