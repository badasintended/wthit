package mcp.mobius.waila.jaded.mixin;

import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.util.CommonProxy;

@Mixin(CommonProxy.class)
public abstract class MixinCommonProxy {

    @Inject(method = "loadComplete", at = @At("HEAD"), cancellable = true, remap = false)
    private void wthit_loadPlugin(FMLLoadCompleteEvent event, CallbackInfo ci) {
        ci.cancel();
    }

}
