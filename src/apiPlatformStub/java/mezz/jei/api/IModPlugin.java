package mezz.jei.api;

import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

public interface IModPlugin {

    ResourceLocation getPluginUid();

    default void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

}
